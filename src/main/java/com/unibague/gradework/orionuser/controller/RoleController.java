package com.unibague.gradework.orionuser.controller;

import com.unibague.gradework.orionuser.model.Role;
import com.unibague.gradework.orionuser.service.IRoleService;
import com.unibague.gradework.orionuser.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * RoleController con soporte para llamadas internas S2S (gateway/orion-auth).
 * - Si viene con X-Internal-Request=true o X-Service-Request=true -> se permite sin UserContext.
 * - Para usuarios finales sigue aplicando UserContext (coordinador/admin).
 * - Soporta ambos paths: /api/roles y /service/role
 */
@Slf4j
@RestController
@RequestMapping({"/api/roles", "/service/role"})
public class RoleController {

    @Autowired
    private IRoleService roleService;

    /* ===================== Helpers ===================== */

    private boolean isInternal(HttpServletRequest request) {
        String internal = request.getHeader("X-Internal-Request");
        String s2s = request.getHeader("X-Service-Request");
        // true en texto plano (gateway y auth los envían así)
        return "true".equalsIgnoreCase(internal) || "true".equalsIgnoreCase(s2s);
    }

    private void requireCoordinatorOrAdmin(UserContext.AuthenticatedUser user) {
        if (!user.isCoordinator() && !user.isAdmin()) {
            throw new SecurityException("Only coordinators and administrators can perform this action");
        }
    }

    /* ===================== CRUD ===================== */

    @PostMapping
    public ResponseEntity<?> createRole(@Valid @RequestBody Role role, HttpServletRequest request) {
        try {
            if (!isInternal(request)) {
                UserContext.requireAdmin();
            }
            Role created = roleService.createRole(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (SecurityException e) {
            return ResponseEntity.status(isInternal(request) ? HttpStatus.FORBIDDEN : HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "ADMIN_REQUIRED", "message", "Only administrators can create roles"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRoles(HttpServletRequest request) {
        try {
            if (!isInternal(request)) {
                UserContext.AuthenticatedUser user = UserContext.requireAuthentication();
                requireCoordinatorOrAdmin(user);
            }
            List<Role> roles = roleService.getAllRoles();
            return ResponseEntity.ok(roles);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable String id, HttpServletRequest request) {
        try {
            if (!isInternal(request)) {
                UserContext.AuthenticatedUser user = UserContext.requireAuthentication();
                requireCoordinatorOrAdmin(user);
            }
            Optional<Role> role = roleService.getRoleById(id);
            return role.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "NOT_FOUND", "message", "Role not found with ID: " + id)));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Endpoint usado por auth: GET /api/roles/name/{name}
     * - Permite llamadas internas del auth/gateway sin UserContext.
     * - Para usuarios finales exige coordinador/admin.
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getRoleByName(@PathVariable String name, HttpServletRequest request) {
        try {
            if (!isInternal(request)) {
                UserContext.AuthenticatedUser user = UserContext.requireAuthentication();
                requireCoordinatorOrAdmin(user);
            }
            if (!StringUtils.hasText(name)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "VALIDATION_ERROR", "message", "Role name is required"));
            }
            Optional<Role> role = roleService.getRoleByName(name);
            return role.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "NOT_FOUND", "message", "Role not found with name: " + name)));
        } catch (SecurityException e) {
            // OJO: antes devolvías 401 aquí; eso rompía al auth (S2S). Con isInternal() ya no caerá aquí.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable String id,
                                        @Valid @RequestBody Role roleDetails,
                                        HttpServletRequest request) {
        try {
            if (!isInternal(request)) {
                UserContext.requireAdmin();
            }
            Role updated = roleService.updateRole(id, roleDetails)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + id));
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "ADMIN_REQUIRED", "message", "Only administrators can update roles"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable String id, HttpServletRequest request) {
        try {
            if (!isInternal(request)) {
                UserContext.requireAdmin();
            }
            roleService.deleteRole(id);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "ADMIN_REQUIRED", "message", "Only administrators can delete roles"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    /* ============ Permisos y utilitarios (mismo patrón) ============ */

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<?> getRolePermissions(@PathVariable String roleId, HttpServletRequest request) {
        try {
            if (!isInternal(request)) {
                UserContext.AuthenticatedUser user = UserContext.requireAuthentication();
                requireCoordinatorOrAdmin(user);
            }
            List<String> permissions = roleService.getRolePermissions(roleId);
            return ResponseEntity.ok(permissions);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<?> addPermissionToRole(@PathVariable String roleId,
                                                 @RequestBody Map<String, String> requestBody,
                                                 HttpServletRequest request) {
        try {
            if (!isInternal(request)) {
                UserContext.requireAdmin();
            }
            String permission = requestBody.get("permission");
            if (!StringUtils.hasText(permission)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "VALIDATION_ERROR", "message", "Permission is required"));
            }
            Role updated = roleService.addPermissionToRole(roleId, permission);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "ADMIN_REQUIRED", "message", "Only administrators can modify permissions"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{roleId}/permissions/{permission}")
    public ResponseEntity<?> removePermissionFromRole(@PathVariable String roleId,
                                                      @PathVariable String permission,
                                                      HttpServletRequest request) {
        try {
            if (!isInternal(request)) {
                UserContext.requireAdmin();
            }
            Role updated = roleService.removePermissionFromRole(roleId, permission);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "ADMIN_REQUIRED", "message", "Only administrators can modify permissions"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    @GetMapping("/{roleId}/exists")
    public ResponseEntity<?> roleExists(@PathVariable String roleId, HttpServletRequest request) {
        try {
            if (!isInternal(request)) {
                UserContext.requireAuthentication();
            }
            boolean exists = roleService.getRoleById(roleId).isPresent();
            return ResponseEntity.ok(Map.of("roleId", roleId, "exists", exists));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }
}
