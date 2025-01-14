package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.Role;
import com.unibague.gradework.orionserver.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The RoleServiceImpl class provides the implementation of the RoleService interface.
 * This service handles the business logic for managing roles in the system.
 *
 * Annotations:
 * - @Service: Marks this class as a Spring service component, enabling it to be injected and managed by the Spring container.
 */
@Service
public class RoleServiceImpl implements RoleService {

    /**
     * The RoleRepository instance for interacting with the "Role" collection in MongoDB.
     * Automatically injected by Spring.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Creates a new role in the system by saving it to the database.
     *
     * @param role the Role object containing the details of the role to be created.
     * @return the created Role object.
     */
    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    /**
     * Retrieves all roles from the database.
     *
     * @return a List of Role objects.
     */
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Retrieves a role by its unique identifier.
     *
     * @param id the unique identifier of the role (as a String).
     * @return an Optional containing the Role object if found, or an empty Optional if not.
     */
    @Override
    public Optional<Role> getRoleById(String id) {
        return roleRepository.findById(Long.valueOf(id));
    }

    /**
     * Updates an existing role by its unique identifier.
     * If the role exists, its details are updated and saved to the database.
     *
     * @param id the unique identifier of the role to be updated (as a String).
     * @param roleDetails a Role object containing the updated details.
     * @return an Optional containing the updated Role object if the update was successful,
     *         or an empty Optional if the role was not found.
     */
    @Override
    public Optional<Role> updateRole(String id, Role roleDetails) {
        return roleRepository.findById(Long.valueOf(id)).map(existingRole -> {
            existingRole.setName(roleDetails.getName());
            return roleRepository.save(existingRole);
        });
    }

    /**
     * Deletes a role by its unique identifier.
     *
     * @param id the unique identifier of the role to be deleted (as a String).
     */
    @Override
    public void deleteRole(String id) {
        roleRepository.deleteById(Long.valueOf(id));
    }
}
