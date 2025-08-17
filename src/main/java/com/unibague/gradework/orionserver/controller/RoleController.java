package com.unibague.gradework.orionserver.controller;

import com.unibague.gradework.orionserver.model.Role;
import com.unibague.gradework.orionserver.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The RoleController class handles HTTP requests related to roles.
 * It acts as a REST controller, providing endpoints to create, retrieve, update, and delete roles.
 *
 * Annotations:
 * - @RestController: Marks this class as a RESTful controller.
 * - @RequestMapping: Specifies the base path for all endpoints in this controller.
 */
@RestController
@RequestMapping("/service/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    /**
     * Creates a new role.
     *
     * @param role the Role object containing the role's details.
     * @return a ResponseEntity containing the created Role object and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
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
    public ResponseEntity<Role> updateRole(@PathVariable String id, @RequestBody Role roleDetails) {
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
}