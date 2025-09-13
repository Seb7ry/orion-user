package com.unibague.gradework.orionuser.controller;

import com.unibague.gradework.orionuser.model.Role;
import com.unibague.gradework.orionuser.service.IRoleService;
import com.unibague.gradework.orionuser.security.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Enhanced RoleController with authentication and authorization
 * Most operations are admin-only for security
 */
@Slf4j
@RestController
@RequestMapping("/service/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    // ==========================================
    // BASIC CRUD OPERATIONS
    // ==========================================

    /**
     * Creates a new role
     * SECURITY: Only ADMINS can create roles
     */
    @PostMapping
    public ResponseEntity<?> createRole(@Valid @RequestBody Role role) {
        try {
            UserContext.requireAdmin();
            UserContext.AuthenticatedUser currentUser = UserContext.getCurrentUser().get();

            log.info("Creating role: {} by admin: {}", role.getName(), currentUser.getUserId());

            Role created = roleService.createRole(role);

            log.info("Role created successfully: {} by admin: {}",
                    created.getName(), currentUser.getUserId());

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "ADMIN_REQUIRED",
                            "message", "Only administrators can create roles"
                    ));
        }
    }

    /**
     * Retrieves all roles
     * SECURITY: COORDINATORS and ADMINS can view roles
     */
    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Only coordinators and admins can view roles
            if (!currentUser.isCoordinator() && !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "Only coordinators and administrators can view roles"
                        ));
            }

            log.debug("Retrieving all roles for user: {} ({})",
                    currentUser.getUserId(), currentUser.getRole());

            List<Role> roles = roleService.getAllRoles();

            return ResponseEntity.ok(roles);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves a role by its ID
     * SECURITY: COORDINATORS and ADMINS can view individual roles
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable String id) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Only coordinators and admins can view roles
            if (!currentUser.isCoordinator() && !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "Only coordinators and administrators can view roles"
                        ));
            }

            log.debug("Retrieving role {} for user: {} ({})",
                    id, currentUser.getUserId(), currentUser.getRole());

            Role role = roleService.getRoleById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + id));

            return ResponseEntity.ok(role);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves a role by its name
     * SECURITY: COORDINATORS and ADMINS can view roles by name (used for auth)
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getRoleByName(@PathVariable String name) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Only coordinators and admins can view roles
            if (!currentUser.isCoordinator() && !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "Only coordinators and administrators can view roles"
                        ));
            }

            log.debug("Retrieving role by name: {} for user: {} ({})",
                    name, currentUser.getUserId(), currentUser.getRole());

            Role role = roleService.getRoleByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found with name: " + name));

            return ResponseEntity.ok(role);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    /**
     * Updates an existing role by its ID
     * SECURITY: Only ADMINS can update roles
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable String id, @Valid @RequestBody Role roleDetails) {
        try {
            UserContext.requireAdmin();
            UserContext.AuthenticatedUser currentUser = UserContext.getCurrentUser().get();

            log.info("Updating role: {} by admin: {}", id, currentUser.getUserId());

            Role updated = roleService.updateRole(id, roleDetails)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + id));

            log.info("Role updated successfully: {} -> {} by admin: {}",
                    id, updated.getName(), currentUser.getUserId());

            return ResponseEntity.ok(updated);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "ADMIN_REQUIRED",
                            "message", "Only administrators can update roles"
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    /**
     * Deletes a role by its ID
     * SECURITY: Only ADMINS can delete roles
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable String id) {
        try {
            UserContext.requireAdmin();
            UserContext.AuthenticatedUser currentUser = UserContext.getCurrentUser().get();

            // Get role info for logging
            Role existingRole = roleService.getRoleById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + id));

            log.warn("DELETING ROLE: {} ({}) by admin: {}",
                    id, existingRole.getName(), currentUser.getUserId());

            roleService.deleteRole(id);

            log.warn("Role deleted successfully: {} by admin: {}",
                    existingRole.getName(), currentUser.getUserId());

            return ResponseEntity.noContent().build();

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "ADMIN_REQUIRED",
                            "message", "Only administrators can delete roles"
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    // ==========================================
    // PERMISSION MANAGEMENT ENDPOINTS
    // ==========================================

    /**
     * Gets all permissions for a specific role
     * SECURITY: COORDINATORS and ADMINS can view permissions
     */
    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<?> getRolePermissions(@PathVariable String roleId) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Only coordinators and admins can view permissions
            if (!currentUser.isCoordinator() && !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "Only coordinators and administrators can view permissions"
                        ));
            }

            log.debug("Retrieving permissions for role: {} by user: {} ({})",
                    roleId, currentUser.getUserId(), currentUser.getRole());

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

    /**
     * Adds a permission to a role
     * SECURITY: Only ADMINS can modify permissions
     */
    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<?> addPermissionToRole(@PathVariable String roleId,
                                                 @RequestBody Map<String, String> request) {
        try {
            UserContext.requireAdmin();
            UserContext.AuthenticatedUser currentUser = UserContext.getCurrentUser().get();

            String permission = request.get("permission");
            if (permission == null || permission.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error", "VALIDATION_ERROR",
                                "message", "Permission is required"
                        ));
            }

            log.info("Adding permission '{}' to role {} by admin: {}",
                    permission, roleId, currentUser.getUserId());

            Role updatedRole = roleService.addPermissionToRole(roleId, permission);

            log.info("Permission '{}' added successfully to role {} by admin: {}",
                    permission, roleId, currentUser.getUserId());

            return ResponseEntity.ok(updatedRole);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "ADMIN_REQUIRED",
                            "message", "Only administrators can modify permissions"
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    /**
     * Removes a permission from a role
     * SECURITY: Only ADMINS can modify permissions
     */
    @DeleteMapping("/{roleId}/permissions/{permission}")
    public ResponseEntity<?> removePermissionFromRole(@PathVariable String roleId,
                                                      @PathVariable String permission) {
        try {
            UserContext.requireAdmin();
            UserContext.AuthenticatedUser currentUser = UserContext.getCurrentUser().get();

            log.info("Removing permission '{}' from role {} by admin: {}",
                    permission, roleId, currentUser.getUserId());

            Role updatedRole = roleService.removePermissionFromRole(roleId, permission);

            log.info("Permission '{}' removed successfully from role {} by admin: {}",
                    permission, roleId, currentUser.getUserId());

            return ResponseEntity.ok(updatedRole);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "ADMIN_REQUIRED",
                            "message", "Only administrators can modify permissions"
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NOT_FOUND", "message", e.getMessage()));
        }
    }

    // ==========================================
    // UTILITY ENDPOINTS
    // ==========================================

    /**
     * Get role statistics (for admin dashboard)
     * SECURITY: Only ADMINS can view role statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getRoleStatistics() {
        try {
            UserContext.requireAdmin();
            UserContext.AuthenticatedUser currentUser = UserContext.getCurrentUser().get();

            log.debug("Retrieving role statistics for admin: {}", currentUser.getUserId());

            List<Role> allRoles = roleService.getAllRoles();

            // Calculate statistics
            int totalRoles = allRoles.size();
            int rolesWithPermissions = (int) allRoles.stream()
                    .filter(role -> role.getPermisos() != null && !role.getPermisos().isEmpty())
                    .count();
            int totalPermissions = allRoles.stream()
                    .mapToInt(role -> role.getPermisos() != null ? role.getPermisos().size() : 0)
                    .sum();

            Map<String, Object> statistics = Map.of(
                    "totalRoles", totalRoles,
                    "rolesWithPermissions", rolesWithPermissions,
                    "rolesWithoutPermissions", totalRoles - rolesWithPermissions,
                    "totalPermissions", totalPermissions,
                    "averagePermissionsPerRole", totalRoles > 0 ? (double) totalPermissions / totalRoles : 0,
                    "requestedBy", currentUser.getUserId(),
                    "requestedAt", java.time.LocalDateTime.now()
            );

            return ResponseEntity.ok(statistics);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "ADMIN_REQUIRED",
                            "message", "Only administrators can view role statistics"
                    ));
        }
    }

    /**
     * Validate if a role exists (utility endpoint)
     * SECURITY: All authenticated users can check if a role exists
     */
    @GetMapping("/{roleId}/exists")
    public ResponseEntity<?> roleExists(@PathVariable String roleId) {
        try {
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            log.debug("Checking existence of role: {} for user: {}", roleId, currentUser.getUserId());

            boolean exists = roleService.getRoleById(roleId).isPresent();

            return ResponseEntity.ok(Map.of(
                    "roleId", roleId,
                    "exists", exists,
                    "checkedBy", currentUser.getUserId()
            ));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }
}