package com.unibague.gradework.orionuser.service;

import com.unibague.gradework.orionuser.model.Role;
import com.unibague.gradework.orionuser.repository.ActorRepository;
import com.unibague.gradework.orionuser.repository.RoleRepository;
import com.unibague.gradework.orionuser.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class ValidationService implements IValidationService {

    private final StudentRepository studentRepository;
    private final ActorRepository actorRepository;
    private final RoleRepository roleRepository;

    public ValidationService(StudentRepository studentRepository,
                             ActorRepository actorRepository,
                             RoleRepository roleRepository) {
        this.studentRepository = studentRepository;
        this.actorRepository = actorRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Role validateRole(Role role) {
        if (role == null || role.getIdRole() == null || role.getIdRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role id is required.");
        }
        return roleRepository.findById(role.getIdRole())
                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + role.getIdRole() + " not found"));
    }

    @Override
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        boolean emailExists = studentRepository.existsByEmail(email) || actorRepository.existsByEmail(email);
        if (emailExists) {
            throw new IllegalArgumentException("A user with email " + email + " already exists.");
        }
    }

    @Override
    public void validateEmailOnUpdate(String existingEmail, String newEmail) {
        if (!existingEmail.equals(newEmail)) {
            boolean exists = studentRepository.existsByEmail(newEmail) || actorRepository.existsByEmail(newEmail);
            if (exists) {
                throw new IllegalArgumentException("The email " + newEmail + " is already in use.");
            }
        }
    }

    @Override
    public void validateIdUser(String idUser) {
        if (idUser == null || idUser.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required.");
        }
        boolean idExists = studentRepository.existsById(idUser) || actorRepository.existsById(idUser);
        if (idExists) {
            throw new IllegalArgumentException("User ID " + idUser + " is already in use.");
        }
    }
}
