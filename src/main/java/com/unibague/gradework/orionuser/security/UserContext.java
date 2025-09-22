package com.unibague.gradework.orionuser.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UserContext para el servicio USER.
 * - Lee los headers que inyecta el Gateway y pobla un ThreadLocal con el usuario autenticado.
 * - Expone helpers como isAdmin(), isCoordinator(), isStudent() y hasAccessToProgram().
 * - Provee requireAuthentication() y requireAdmin() para validaciones rápidas en controladores.
 * - Incluye métodos estáticos populateFrom(req) y clear() para compatibilidad con SecurityConfig.
 */
@Slf4j
@Component
@Order(2) // corre después del GatewaySecurityFilter (@Order(1))
public class UserContext extends OncePerRequestFilter {

    private static final ThreadLocal<AuthenticatedUser> CTX = new ThreadLocal<>();

    // Headers que pone el gateway
    private static final String H_USER_ID       = "X-User-ID";
    private static final String H_USER_EMAIL    = "X-User-Email";
    private static final String H_USER_ROLE     = "X-User-Role";
    private static final String H_USER_PROGRAMS = "X-User-Programs";
    private static final String H_GW_VALIDATED  = "X-Gateway-Validated";

    public enum Role {
        ADMIN, COORDINATOR, STUDENT, UNKNOWN;

        public static Role from(String raw) {
            if (raw == null) return UNKNOWN;
            return switch (raw.trim().toUpperCase(Locale.ROOT)) {
                case "ADMIN" -> ADMIN;
                case "COORDINATOR", "COORD" -> COORDINATOR;
                case "STUDENT", "ALUMNO" -> STUDENT;
                default -> UNKNOWN;
            };
        }
    }

    @Getter
    @ToString
    public static class AuthenticatedUser {
        private final String userId;
        private final String email;
        private final Role role;
        private final Set<String> programIds; // IDs a los que tiene acceso (si aplica)

        public AuthenticatedUser(String userId, String email, Role role, Set<String> programIds) {
            this.userId = userId;
            this.email = email;
            this.role = role == null ? Role.UNKNOWN : role;
            this.programIds = programIds == null ? Set.of() : Set.copyOf(programIds);
        }

        // === Helpers usados por los controladores ===
        public boolean isAdmin()       { return role == Role.ADMIN; }
        public boolean isCoordinator() { return role == Role.COORDINATOR; }
        public boolean isStudent()     { return role == Role.STUDENT; }

        /** Verifica si el usuario tiene acceso a un programa específico */
        public boolean hasAccessToProgram(String programId) {
            if (programId == null || programId.isBlank()) return false;
            if (isAdmin() || isCoordinator()) return true; // coordinadores ven todo por simplicidad
            return programIds.contains(programId);
        }
    }

    // ==== API estática para usar en controladores ====

    /** Obtiene el usuario actual si existe */
    public static Optional<AuthenticatedUser> getCurrentUser() {
        return Optional.ofNullable(CTX.get());
    }

    /** Lanza SecurityException si no hay usuario */
    public static AuthenticatedUser requireAuthentication() {
        AuthenticatedUser u = CTX.get();
        if (u == null) throw new SecurityException("User not authenticated");
        return u;
    }

    /** Lanza SecurityException si no es admin */
    public static void requireAdmin() {
        AuthenticatedUser u = requireAuthentication();
        if (!u.isAdmin()) throw new SecurityException("Admin privileges required");
    }

    // ==== Compatibilidad con SecurityConfig (métodos estáticos antiguos) ====

    /** Compat: poblar el contexto manualmente desde un HttpServletRequest */
    public static void populateFrom(HttpServletRequest req) {
        String gw = header(req, H_GW_VALIDATED);

        String userId    = header(req, H_USER_ID);
        String email     = header(req, H_USER_EMAIL);
        Role   role      = Role.from(header(req, H_USER_ROLE));
        Set<String> programs = parsePrograms(header(req, H_USER_PROGRAMS));

        if ("true".equalsIgnoreCase(gw) && userId != null) {
            AuthenticatedUser au = new AuthenticatedUser(userId, email, role, programs);
            CTX.set(au);
            log.debug("UserContext (static) populated: {}", au);
        } else {
            CTX.remove();
            log.debug("UserContext (static) not populated (gw={}, userId={})", gw, userId);
        }
    }

    /** Compat: limpiar el ThreadLocal */
    public static void clear() {
        CTX.remove();
    }

    // ==== Filter ====

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        try {
            // Reusar la misma lógica de compatibilidad
            populateFrom(req);
            chain.doFilter(req, res);
        } finally {
            clear(); // limpieza para no filtrar entre hilos
        }
    }

    private static String header(HttpServletRequest req, String name) {
        String v = req.getHeader(name);
        return (v == null || v.isBlank()) ? null : v;
    }

    /** Parse de programas desde un header tipo: "prog1,prog2, prog3" o "[]"/"["a","b"]" */
    private static Set<String> parsePrograms(String raw) {
        if (raw == null || raw.isBlank()) return Set.of();

        String s = raw.trim();

        // intenta detectar JSON simple tipo ["a","b"]
        if ((s.startsWith("[") && s.endsWith("]"))) {
            s = s.substring(1, s.length() - 1).trim();
            if (s.isEmpty()) return Set.of();
            // quita comillas y espacios
            return Arrays.stream(s.split(","))
                    .map(String::trim)
                    .map(x -> x.replace("\"", "").replace("'", ""))
                    .filter(x -> !x.isBlank())
                    .collect(Collectors.toSet());
        }

        // CSV simple: a,b,c
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(x -> !x.isBlank())
                .collect(Collectors.toSet());
    }
}
