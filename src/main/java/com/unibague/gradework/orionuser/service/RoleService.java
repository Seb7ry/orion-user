package com.unibague.gradework.orionuser.service;

import com.unibague.gradework.orionuser.exception.UserExceptions;
import com.unibague.gradework.orionuser.model.Role;
import com.unibague.gradework.orionuser.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing roles and permissions
 * Provides CRUD operations and permission management for roles
 */
@Slf4j
@Service
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // ==========================================
    // BASIC CRUD OPERATIONS
    // ==========================================

    @Override
    public Role createRole(Role role) {
        if (role == null || role.getName() == null || role.getName().isBlank()) {
            throw new UserExceptions.InvalidUserDataException("Role name is required");
        }

        // Check if role with same name already exists
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new UserExceptions.DuplicateUserException("name", role.getName());
        }

        // Initialize empty permissions list if null
        if (role.getPermisos() == null) {
            role.setPermisos(new ArrayList<>());
        }

        log.info("Creating role: {} with {} permissions", role.getName(), role.getPermisos().size());
        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        log.debug("Retrieving all roles");
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(String id) {
        if (id == null || id.isBlank()) {
            throw new UserExceptions.InvalidUserDataException("Role ID cannot be empty");
        }
        log.debug("Retrieving role by ID: {}", id);
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        if (name == null || name.isBlank()) {
            throw new UserExceptions.InvalidUserDataException("Role name cannot be empty");
        }
        log.debug("Retrieving role by name: {}", name);
        return roleRepository.findByName(name);
    }

    @Override
    public Optional<Role> updateRole(String id, Role roleDetails) {
        if (id == null || id.isBlank()) {
            throw new UserExceptions.InvalidUserDataException("Role ID cannot be empty");
        }

        if (roleDetails == null || roleDetails.getName() == null || roleDetails.getName().isBlank()) {
            throw new UserExceptions.InvalidUserDataException("Role name is required for update");
        }

        return roleRepository.findById(id).map(existingRole -> {
            // Check if new name conflicts with existing role (excluding current)
            Optional<Role> existingByName = roleRepository.findByName(roleDetails.getName());
            if (existingByName.isPresent() && !existingByName.get().getIdRole().equals(id)) {
                throw new UserExceptions.DuplicateUserException("name", roleDetails.getName());
            }

            existingRole.setName(roleDetails.getName());

            // Update permissions (preserve existing if null)
            if (roleDetails.getPermisos() != null) {
                existingRole.setPermisos(new ArrayList<>(roleDetails.getPermisos()));
            }

            log.info("Updating role: {} with {} permissions", existingRole.getName(),
                    existingRole.getPermisos().size());
            return roleRepository.save(existingRole);
        });
    }

    @Override
    public void deleteRole(String id) {
        if (id == null || id.isBlank()) {
            throw new UserExceptions.InvalidUserDataException("Role ID cannot be empty for deletion");
        }

        if (!roleRepository.existsById(id)) {
            throw new UserExceptions.RoleNotFoundException(id);
        }

        log.info("Deleting role with ID: {}", id);
        roleRepository.deleteById(id);
    }

    // ==========================================
    // PERMISSION MANAGEMENT METHODS
    // ==========================================

    @Override
    public Role addPermissionToRole(String roleId, String permission) {
        if (permission == null || permission.isBlank()) {
            throw new UserExceptions.InvalidUserDataException("Permission cannot be empty");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserExceptions.RoleNotFoundException(roleId));

        if (role.getPermisos() == null) {
            role.setPermisos(new ArrayList<>());
        }

        if (!role.getPermisos().contains(permission)) {
            role.getPermisos().add(permission);
            log.info("Added permission '{}' to role '{}'", permission, role.getName());
            return roleRepository.save(role);
        }

        log.debug("Permission '{}' already exists in role '{}'", permission, role.getName());
        return role;
    }

    @Override
    public Role removePermissionFromRole(String roleId, String permission) {
        if (permission == null || permission.isBlank()) {
            throw new UserExceptions.InvalidUserDataException("Permission cannot be empty");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserExceptions.RoleNotFoundException(roleId));

        if (role.getPermisos() != null && role.getPermisos().remove(permission)) {
            log.info("Removed permission '{}' from role '{}'", permission, role.getName());
            return roleRepository.save(role);
        }

        log.debug("Permission '{}' not found in role '{}'", permission, role.getName());
        return role;
    }

    @Override
    public List<String> getRolePermissions(String roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserExceptions.RoleNotFoundException(roleId));

        return role.getPermisos() != null ? new ArrayList<>(role.getPermisos()) : new ArrayList<>();
    }
}