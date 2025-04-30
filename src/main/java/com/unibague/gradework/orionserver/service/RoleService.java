package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.Role;
import com.unibague.gradework.orionserver.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(Role role) {
        if (role == null || role.getName() == null || role.getName().isBlank()) {
            throw new IllegalArgumentException("El rol debe tener un nombre válido.");
        }
        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del rol no puede estar vacío.");
        }
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío.");
        }

        return roleRepository.findByName(name);
    }

    @Override
    public Optional<Role> updateRole(String id, Role roleDetails) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del rol no puede estar vacío.");
        }

        if (roleDetails == null || roleDetails.getName() == null || roleDetails.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio para actualizar.");
        }

        return roleRepository.findById(id).map(existingRole -> {
            existingRole.setName(roleDetails.getName());
            return roleRepository.save(existingRole);
        });
    }

    @Override
    public void deleteRole(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del rol no puede estar vacío para eliminar.");
        }
        roleRepository.deleteById(id);
    }
}