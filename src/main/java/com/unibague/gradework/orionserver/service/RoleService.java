package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * The RoleService interface defines the business logic for managing roles in the system.
 * It provides methods for creating, retrieving, updating, and deleting roles.
 */
public interface RoleService {

    /**
     * Creates a new role in the system.
     *
     * @param role the Role object containing the details of the role to be created.
     * @return the created Role object.
     */
    Role createRole(Role role);

    /**
     * Retrieves a list of all roles in the system.
     *
     * @return a List of Role objects.
     */
    List<Role> getAllRoles();

    /**
     * Retrieves a role by its unique identifier.
     *
     * @param id the unique identifier of the role.
     * @return an Optional containing the Role object if found, or an empty Optional if not.
     */
    Optional<Role> getRoleById(String id);

    /**
     * Updates an existing role by its unique identifier.
     *
     * @param id the unique identifier of the role to be updated.
     * @param roleDetails a Role object containing the updated details.
     * @return an Optional containing the updated Role object if the update was successful,
     *         or an empty Optional if the role was not found.
     */
    Optional<Role> updateRole(String id, Role roleDetails);

    /**
     * Deletes a role by its unique identifier.
     *
     * @param id the unique identifier of the role to be deleted.
     */
    void deleteRole(String id);
}
