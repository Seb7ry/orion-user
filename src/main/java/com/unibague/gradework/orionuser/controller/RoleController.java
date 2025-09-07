package com.unibague.gradework.orionuser.controller;

import com.unibague.gradework.orionuser.model.Role;
import com.unibague.gradework.orionuser.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * The RoleController class handles HTTP requests related to roles and permissions.
 * It acts as a REST controller, providing endpoints to create, retrieve, update, and delete roles,
 * as well as manage permissions for each role.
 */
@RestController
@RequestMapping("/service/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    // ==========================================
    // BASIC CRUD OPERATIONS
    // ==========================================

    /**
     * Creates a new role.
     *
     * @param role the Role object containing the role's details.
     * @return a ResponseEntity containing the created Role object and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
        return new ResponseEntity<>(roleService.createRole(role), HttpStatus.CREATED);
    }

    /**
     * Retrieves all roles.
     *
     * @return a ResponseEntity containing a list of all Role objects and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

    /**
     * Retrieves a role by its ID.
     *
     * @param id the unique identifier of the role.
     * @return a ResponseEntity containing the Role object and HTTP status 200 (OK) if found,
     *         or HTTP status 404 (Not Found) if the role does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable String id) {
        return roleService.getRoleById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + id));
    }

    /**
     * Retrieves a role by its name.
     *
     * @param name the name of the role.
     * @return a ResponseEntity containing the Role object and HTTP status 200 (OK) if found,
     *         or HTTP status 404 (Not Found) if the role does not exist.
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with name: " + name));
    }

    /**
     * Updates an existing role by its ID.
     *
     * @param id the unique identifier of the role to update.
     * @param roleDetails the Role object containing the updated details.
     * @return a ResponseEntity containing the updated Role object and HTTP status 200 (OK) if the update is successful,
     *         or HTTP status 404 (Not Found) if the role does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable String id, @Valid @RequestBody Role roleDetails) {
        Role updated = roleService.updateRole(id, roleDetails)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + id));
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a role by its ID.
     *
     * @param id the unique identifier of the role to delete.
     * @return a ResponseEntity with HTTP status 204 (No Content) if the deletion is successful.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // PERMISSION MANAGEMENT ENDPOINTS
    // ==========================================

    /**
     * Gets all permissions for a specific role.
     *
     * @param roleId the unique identifier of the role.
     * @return a ResponseEntity containing the list of permissions and HTTP status 200 (OK).
     */
    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<List<String>> getRolePermissions(@PathVariable String roleId) {
        List<String> permissions = roleService.getRolePermissions(roleId);
        return ResponseEntity.ok(permissions);
    }

    /**
     * Adds a permission to a role.
     *
     * @param roleId the unique identifier of the role.
     * @param request the request body containing the permission to add.
     * @return a ResponseEntity containing the updated Role object and HTTP status 200 (OK).
     */
    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<Role> addPermissionToRole(@PathVariable String roleId,
                                                    @RequestBody Map<String, String> request) {
        String permission = request.get("permission");
        if (permission == null || permission.isBlank()) {
            throw new IllegalArgumentException("Permission is required");
        }

        Role updatedRole = roleService.addPermissionToRole(roleId, permission);
        return ResponseEntity.ok(updatedRole);
    }

    /**
     * Removes a permission from a role.
     *
     * @param roleId the unique identifier of the role.
     * @param permission the permission to remove.
     * @return a ResponseEntity containing the updated Role object and HTTP status 200 (OK).
     */
    @DeleteMapping("/{roleId}/permissions/{permission}")
    public ResponseEntity<Role> removePermissionFromRole(@PathVariable String roleId,
                                                         @PathVariable String permission) {
        Role updatedRole = roleService.removePermissionFromRole(roleId, permission);
        return ResponseEntity.ok(updatedRole);
    }
}