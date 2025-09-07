package com.unibague.gradework.orionuser.service;

import com.unibague.gradework.orionuser.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Role management including permissions
 */
public interface IRoleService {

    // Basic CRUD operations
    Role createRole(Role role);
    List<Role> getAllRoles();
    Optional<Role> getRoleById(String id);
    Optional<Role> getRoleByName(String name);
    Optional<Role> updateRole(String id, Role roleDetails);
    void deleteRole(String id);

    // Permission management methods
    Role addPermissionToRole(String roleId, String permission);
    Role removePermissionFromRole(String roleId, String permission);
    List<String> getRolePermissions(String roleId);
}