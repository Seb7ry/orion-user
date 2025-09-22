package com.unibague.gradework.orionuser.controller;

import com.unibague.gradework.orionuser.model.*;
import com.unibague.gradework.orionuser.service.*;
import com.unibague.gradework.orionuser.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Enhanced UserController with authentication and authorization
 * Handles user management with role-based access control
 */
@Slf4j
@RestController
@RequestMapping("/service/user")
public class UserController {

    @Autowired private IUserService userService;
    @Autowired private IRoleService roleService;

    // Helper para detectar llamadas internas S2S desde el gateway/orion-auth
    private boolean isInternal(HttpServletRequest req) {
        String a = req.getHeader("X-Internal-Request");
        String b = req.getHeader("X-Service-Request");
        return "true".equalsIgnoreCase(a) || "true".equalsIgnoreCase(b);
    }

    // ==========================================
    // STUDENT MANAGEMENT
    // ==========================================

    /**
     * Creates a new student
     * SECURITY:
     *  - S2S (ROLE_SERVICE): permitido (creación automática desde auth)
     *  - Usuarios humanos: solo COORDINATOR o ADMIN
     */
    @PostMapping("/student")
    public ResponseEntity<?> createStudent(@RequestBody Student student, HttpServletRequest req) {
        try {
            if (!isInternal(req)) {
                // Flujo usuario humano vía JWT
                UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

                if (!currentUser.isCoordinator() && !currentUser.isAdmin()) {
                    log.warn("UNAUTHORIZED: User {} ({}) attempted to create student",
                            currentUser.getUserId(), currentUser.getRole());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of(
                                    "error", "INSUFFICIENT_PERMISSIONS",
                                    "message", "Only coordinators and administrators can create students"
                            ));
                }
                log.info("Creating student (user flow): {} by user: {} ({})",
                        student.getEmail(), currentUser.getUserId(), currentUser.getRole());
            } else {
                // Flujo S2S
                log.info("Creating student (S2S): {}", student.getEmail());
            }

            // Validación de rol (por id) — si solo tienes el nombre, haz lookup previo por nombre
            Role role = roleService.getRoleById(student.getRole().getIdRole())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + student.getRole().getIdRole()));
            student.setRole(role);

            Student created = userService.createStudent(student);
            log.info("Student created successfully: {}", created.getIdUser());

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "VALIDATION_ERROR", "message", e.getMessage()));
        }
    }

    /**
     * Creates a new actor (teacher/staff)
     * SECURITY:
     *  - S2S (ROLE_SERVICE): permitido (creación automática desde auth)
     *  - Usuarios humanos: solo COORDINATOR o ADMIN
     */
    @PostMapping("/actor")
    public ResponseEntity<?> createActor(@RequestBody Actor actor, HttpServletRequest req) {
        try {
            if (!isInternal(req)) {
                UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

                if (!currentUser.isCoordinator() && !currentUser.isAdmin()) {
                    log.warn("UNAUTHORIZED: User {} ({}) attempted to create actor",
                            currentUser.getUserId(), currentUser.getRole());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of(
                                    "error", "INSUFFICIENT_PERMISSIONS",
                                    "message", "Only coordinators and administrators can create actors"
                            ));
                }
                log.info("Creating actor (user flow): {} by user: {} ({})",
                        actor.getEmail(), currentUser.getUserId(), currentUser.getRole());
            } else {
                log.info("Creating actor (S2S): {}", actor.getEmail());
            }

            // Validación de rol (por id)
            Role role = roleService.getRoleById(actor.getRole().getIdRole())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + actor.getRole().getIdRole()));
            actor.setRole(role);

            Actor created = userService.createActor(actor);
            log.info("Actor created successfully: {}", created.getIdUser());

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "VALIDATION_ERROR", "message", e.getMessage()));
        }
    }

    // ==========================================
    // USER LISTING (with filtering)
    // ==========================================

    /**
     * Retrieves all students with role-based filtering
     * SECURITY:
     * - ADMINS: See all students
     * - COORDINATORS: See students in their programs
     * - TEACHERS: See students in their programs
     * - STUDENTS: Forbidden
     */
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudentsDTO() {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            if (currentUser.isStudent()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "Students cannot view student lists"
                        ));
            }

            log.debug("Retrieving students for user: {} ({})",
                    currentUser.getUserId(), currentUser.getRole());

            List<StudentDTO> students = userService.getAllStudentsDTO();

            if (!currentUser.isAdmin()) {
                students = students.stream()
                        .filter(student -> student.getPrograms().stream()
                                .anyMatch(program -> currentUser.hasAccessToProgram(program.getProgramId())))
                        .toList();

                log.debug("Filtered to {} students for user access", students.size());
            }

            return ResponseEntity.ok(students);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves all actors with role-based filtering
     * SECURITY: Similar to students but more permissive for actors
     */
    @GetMapping("/actors")
    public ResponseEntity<?> getAllActorsDTO() {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            if (!currentUser.isCoordinator() && !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "Only coordinators and administrators can view actor lists"
                        ));
            }

            log.debug("Retrieving actors for user: {} ({})",
                    currentUser.getUserId(), currentUser.getRole());

            List<ActorDTO> actors = userService.getAllActorsDTO();

            if (!currentUser.isAdmin()) {
                actors = actors.stream()
                        .filter(actor -> actor.getPrograms().stream()
                                .anyMatch(program -> currentUser.hasAccessToProgram(program.getProgramId())))
                        .toList();
            }

            return ResponseEntity.ok(actors);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    // ==========================================
    // INDIVIDUAL USER ACCESS
    // ==========================================

    /**
     * Retrieves student by ID with access control
     */
    @GetMapping("/student/{id}")
    public ResponseEntity<?> getStudentDTOById(@PathVariable String id) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            log.debug("Student {} requested by user: {} ({})",
                    id, currentUser.getUserId(), currentUser.getRole());

            StudentDTO student = userService.getStudentDTOById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

            if (!currentUser.isAdmin() && !currentUser.getUserId().equals(id)) {
                boolean hasAccess = student.getPrograms().stream()
                        .anyMatch(program -> currentUser.hasAccessToProgram(program.getProgramId()));

                if (!hasAccess) {
                    log.warn("ACCESS DENIED: User {} attempted to access student {}",
                            currentUser.getUserId(), id);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of(
                                    "error", "ACCESS_DENIED",
                                    "message", "You don't have access to this student's information"
                            ));
                }
            }

            return ResponseEntity.ok(student);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves actor by ID with access control
     */
    @GetMapping("/actor/{id}")
    public ResponseEntity<?> getActorDTOById(@PathVariable String id) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            log.debug("Actor {} requested by user: {} ({})",
                    id, currentUser.getUserId(), currentUser.getRole());

            ActorDTO actor = userService.getActorDTOById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Actor not found with ID: " + id));

            if (!currentUser.isAdmin() && !currentUser.isCoordinator() &&
                    !currentUser.getUserId().equals(id)) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "ACCESS_DENIED",
                                "message", "You don't have access to this actor's information"
                        ));
            }

            return ResponseEntity.ok(actor);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    // ==========================================
    // AUTHENTICATION ENDPOINTS
    // ==========================================

    /**
     * Internal endpoint for authentication service
     * SECURITY: Used by auth service, requires gateway validation
     */
    @GetMapping("/student/auth/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable String id) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            log.debug("Auth request for student {} by user: {}", id, currentUser.getUserId());

            return userService.getStudentById(id)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    /**
     * Internal endpoint for authentication service
     */
    @GetMapping("/actor/auth/{id}")
    public ResponseEntity<?> getActorById(@PathVariable String id) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            log.debug("Auth request for actor {} by user: {}", id, currentUser.getUserId());

            return userService.getActorById(id)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new IllegalArgumentException("Actor not found with ID: " + id));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    /**
     * Get user programs - used for authorization
     * SECURITY: Users can access their own programs or admins can access any
     */
    @GetMapping("/{userId}/programs")
    public ResponseEntity<?> getUserPrograms(@PathVariable String userId) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            if (!currentUser.isAdmin() && !currentUser.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "ACCESS_DENIED",
                                "message", "You can only access your own program information"
                        ));
            }

            log.debug("Programs for user {} requested by: {}", userId, currentUser.getUserId());

            Optional<? extends User> userOpt = userService.getStudentById(userId)
                    .map(s -> (User) s)
                    .or(() -> userService.getActorById(userId).map(a -> (User) a));

            return userOpt
                    .map(user -> ResponseEntity.ok(user.getPrograms()))
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    /**
     * Get user by email - primarily for authentication service
     * SECURITY: Restricted access
     */
    @GetMapping("/auth/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            // Si hay user autenticado (flujo humano), solo su propio email o admin
            Optional<UserContext.AuthenticatedUser> currentUserOpt = UserContext.getCurrentUser();

            if (currentUserOpt.isPresent()) {
                UserContext.AuthenticatedUser currentUser = currentUserOpt.get();

                if (!currentUser.isAdmin() && !currentUser.getUserId().equals(email)) {
                    log.warn("Unauthorized email access attempt: {} requested by {}",
                            email, currentUser.getUserId());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of(
                                    "error", "ACCESS_DENIED",
                                    "message", "You can only access your own user information"
                            ));
                }

                log.debug("Authenticated user {} requesting email: {}", currentUser.getUserId(), email);
            } else {
                // Flujo S2S (internal auth service)
                log.debug("Internal auth service request for email: {}", email);
                return userService.findUserByEmail(email)
                        .map(user -> {
                            log.debug("User found for internal auth request: {}", user.getIdUser());
                            return ResponseEntity.ok(user);
                        })
                        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
            }

            // Flujo normal autenticado
            log.debug("User by email {} requested by authenticated user", email);

            return userService.findUserByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error in getUserByEmail: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "INTERNAL_ERROR", "message", "An unexpected error occurred"));
        }
    }

    // ==========================================
    // UPDATE OPERATIONS
    // ==========================================

    /**
     * Updates a student
     * SECURITY: Users can update their own data, admins can update any
     */
    @PutMapping("/student/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody Student studentDetails) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            if (!currentUser.isAdmin() && !currentUser.getUserId().equals(id)) {
                if (currentUser.isCoordinator()) {
                    StudentDTO existingStudent = userService.getStudentDTOById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Student not found"));

                    boolean hasAccess = existingStudent.getPrograms().stream()
                            .anyMatch(program -> currentUser.hasAccessToProgram(program.getProgramId()));

                    if (!hasAccess) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "ACCESS_DENIED"));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of(
                                    "error", "ACCESS_DENIED",
                                    "message", "You can only update your own information"
                            ));
                }
            }

            log.info("Updating student: {} by user: {} ({})",
                    id, currentUser.getUserId(), currentUser.getRole());

            Student updated = userService.updateStudent(id, studentDetails);

            log.info("Student updated successfully: {} by user: {}", id, currentUser.getUserId());
            return ResponseEntity.ok(updated);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    /**
     * Updates an actor
     * SECURITY: Similar to student updates
     */
    @PutMapping("/actor/{id}")
    public ResponseEntity<?> updateActor(@PathVariable String id, @RequestBody Actor actorDetails) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            if (!currentUser.isAdmin() && !currentUser.getUserId().equals(id) &&
                    !currentUser.isCoordinator()) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "ACCESS_DENIED",
                                "message", "You don't have permission to update this actor"
                        ));
            }

            log.info("Updating actor: {} by user: {} ({})",
                    id, currentUser.getUserId(), currentUser.getRole());

            Actor updated = userService.updateActor(id, actorDetails);

            log.info("Actor updated successfully: {} by user: {}", id, currentUser.getUserId());
            return ResponseEntity.ok(updated);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    // ==========================================
    // DELETE OPERATIONS (Admin only)
    // ==========================================

    /**
     * Deletes a student
     * SECURITY: Only ADMINS can delete users
     */
    @DeleteMapping("/student/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable String id) {
        try {
            UserContext.requireAdmin();
            UserContext.AuthenticatedUser currentUser = UserContext.getCurrentUser().get();

            log.warn("DELETING STUDENT: {} by admin: {}", id, currentUser.getUserId());

            userService.deleteStudent(id);

            log.warn("Student deleted successfully: {} by admin: {}", id, currentUser.getUserId());
            return ResponseEntity.noContent().build();

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "ADMIN_REQUIRED",
                            "message", "Only administrators can delete users"
                    ));
        }
    }

    /**
     * Deletes an actor
     * SECURITY: Only ADMINS can delete users
     */
    @DeleteMapping("/actor/{id}")
    public ResponseEntity<?> deleteActor(@PathVariable String id) {
        try {
            UserContext.requireAdmin();
            UserContext.AuthenticatedUser currentUser = UserContext.getCurrentUser().get();

            log.warn("DELETING ACTOR: {} by admin: {}", id, currentUser.getUserId());

            userService.deleteActor(id);

            log.warn("Actor deleted successfully: {} by admin: {}", id, currentUser.getUserId());
            return ResponseEntity.noContent().build();

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "ADMIN_REQUIRED",
                            "message", "Only administrators can delete users"
                    ));
        }
    }
}
