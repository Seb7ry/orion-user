package com.unibague.gradework.orionuser.service;

import com.unibague.gradework.orionuser.configuration.ServiceProperties;
import com.unibague.gradework.orionuser.exception.UserExceptions;
import com.unibague.gradework.orionuser.model.Role;
import com.unibague.gradework.orionuser.repository.ActorRepository;
import com.unibague.gradework.orionuser.repository.RoleRepository;
import com.unibague.gradework.orionuser.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for validating user-related data
 * Provides centralized validation logic with specific exceptions
 */
@Slf4j
@Service
public class ValidationService implements IValidationService {

    private final StudentRepository studentRepository;
    private final ActorRepository actorRepository;
    private final RoleRepository roleRepository;
    private final ServiceProperties serviceProperties;

    public ValidationService(StudentRepository studentRepository,
                             ActorRepository actorRepository,
                             RoleRepository roleRepository,
                             ServiceProperties serviceProperties) {
        this.studentRepository = studentRepository;
        this.actorRepository = actorRepository;
        this.roleRepository = roleRepository;
        this.serviceProperties = serviceProperties;
    }

    @Override
    public Role validateRole(Role role) {
        if (role == null || role.getIdRole() == null || role.getIdRole().trim().isEmpty()) {
            throw new UserExceptions.InvalidUserDataException("Role ID is required");
        }

        return roleRepository.findById(role.getIdRole())
                .orElseThrow(() -> new UserExceptions.RoleNotFoundException(role.getIdRole()));
    }

    @Override
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UserExceptions.InvalidUserDataException("Email is required");
        }

        // Validate email domain if strict validation is enabled
        if (serviceProperties.isStrictEmailValidation()) {
            String expectedDomain = serviceProperties.getEmailDomain();
            if (!email.toLowerCase().endsWith(expectedDomain.toLowerCase())) {
                throw new UserExceptions.InvalidEmailException(email, expectedDomain);
            }
        }

        // Check for duplicates
        boolean emailExists = studentRepository.existsByEmail(email) || actorRepository.existsByEmail(email);
        if (emailExists) {
            throw new UserExceptions.DuplicateUserException(email);
        }
    }

    @Override
    public void validateEmailOnUpdate(String existingEmail, String newEmail) {
        if (!existingEmail.equals(newEmail)) {
            // Validate domain if strict validation is enabled
            if (serviceProperties.isStrictEmailValidation()) {
                String expectedDomain = serviceProperties.getEmailDomain();
                if (!newEmail.toLowerCase().endsWith(expectedDomain.toLowerCase())) {
                    throw new UserExceptions.InvalidEmailException(newEmail, expectedDomain);
                }
            }

            // Check for duplicates
            boolean exists = studentRepository.existsByEmail(newEmail) || actorRepository.existsByEmail(newEmail);
            if (exists) {
                throw new UserExceptions.DuplicateUserException("email", newEmail);
            }
        }
    }

    @Override
    public void validateIdUser(String idUser) {
        if (idUser == null || idUser.trim().isEmpty()) {
            throw new UserExceptions.InvalidUserDataException("User ID is required");
        }

        boolean idExists = studentRepository.existsById(idUser) || actorRepository.existsById(idUser);
        if (idExists) {
            throw new UserExceptions.DuplicateUserException("ID", idUser);
        }
    }

    @Override
    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new UserExceptions.InvalidPasswordException("Password is required");
        }

        if (password.length() < serviceProperties.getPasswordMinLength()) {
            throw new UserExceptions.InvalidPasswordException(
                    "Password must be at least " + serviceProperties.getPasswordMinLength() + " characters long"
            );
        }
    }

    @Override
    public void validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new UserExceptions.InvalidUserDataException("Student ID is required");
        }

        if (!studentId.matches("^[0-9]{8,12}$")) {
            throw new UserExceptions.InvalidUserDataException("Student ID must be 8-12 digits");
        }

        boolean exists = studentRepository.findAll().stream()
                .anyMatch(student -> studentId.equals(student.getStudentID()));

        if (exists) {
            throw new UserExceptions.DuplicateStudentException(studentId);
        }
    }
}