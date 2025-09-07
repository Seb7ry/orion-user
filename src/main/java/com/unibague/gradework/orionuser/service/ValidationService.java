package com.unibague.gradework.orionuser.service;

import com.unibague.gradework.orionuser.configuration.ServiceProperties;
import com.unibague.gradework.orionuser.exception.UserExceptions;
import com.unibague.gradework.orionuser.model.Role;
import com.unibague.gradework.orionuser.repository.ActorRepository;
import com.unibague.gradework.orionuser.repository.RoleRepository;
import com.unibague.gradework.orionuser.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        log.debug("Validating role: {}", role != null ? role.getIdRole() : "null");

        if (role == null || role.getIdRole() == null || role.getIdRole().trim().isEmpty()) {
            throw new UserExceptions.InvalidUserDataException("Role ID is required");
        }

        Role validatedRole = roleRepository.findById(role.getIdRole())
                .orElseThrow(() -> new UserExceptions.RoleNotFoundException(role.getIdRole()));

        log.debug("Role validation successful: {}", validatedRole.getName());
        return validatedRole;
    }

    @Override
    public void validateEmail(String email) {
        log.debug("Validating email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new UserExceptions.InvalidUserDataException("Email is required");
        }

        String cleanEmail = email.trim().toLowerCase();

        // Domain validation if strict validation is enabled
        if (serviceProperties.isStrictEmailValidation()) {
            String expectedDomain = serviceProperties.getEmailDomain();
            if (!cleanEmail.endsWith(expectedDomain.toLowerCase())) {
                throw new UserExceptions.InvalidEmailException(cleanEmail, expectedDomain);
            }
        }

        // Check for duplicates
        boolean emailExists = studentRepository.existsByEmail(cleanEmail) ||
                actorRepository.existsByEmail(cleanEmail);
        if (emailExists) {
            throw new UserExceptions.DuplicateUserException(cleanEmail);
        }

        log.debug("Email validation successful: {}", cleanEmail);
    }

    @Override
    public void validateEmailOnUpdate(String existingEmail, String newEmail) {
        log.debug("Validating email update: {} -> {}", existingEmail, newEmail);

        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new UserExceptions.InvalidUserDataException("Email is required");
        }

        String cleanExistingEmail = existingEmail.trim().toLowerCase();
        String cleanNewEmail = newEmail.trim().toLowerCase();

        // If email hasn't changed, no validation needed
        if (cleanExistingEmail.equals(cleanNewEmail)) {
            return;
        }

        // Domain validation if strict validation is enabled
        if (serviceProperties.isStrictEmailValidation()) {
            String expectedDomain = serviceProperties.getEmailDomain();
            if (!cleanNewEmail.endsWith(expectedDomain.toLowerCase())) {
                throw new UserExceptions.InvalidEmailException(cleanNewEmail, expectedDomain);
            }
        }

        // Check uniqueness (excluding current user)
        boolean exists = studentRepository.existsByEmail(cleanNewEmail) ||
                actorRepository.existsByEmail(cleanNewEmail);
        if (exists) {
            throw new UserExceptions.DuplicateUserException("email", cleanNewEmail);
        }

        log.debug("Email update validation successful: {}", cleanNewEmail);
    }

    @Override
    public void validateIdUser(String idUser) {
        log.debug("Validating user ID: {}", idUser);

        if (idUser == null || idUser.trim().isEmpty()) {
            throw new UserExceptions.InvalidUserDataException("User ID is required");
        }

        String cleanId = idUser.trim();

        boolean idExists = studentRepository.existsById(cleanId) ||
                actorRepository.existsById(cleanId);
        if (idExists) {
            throw new UserExceptions.DuplicateUserException("ID", cleanId);
        }

        log.debug("User ID validation successful: {}", cleanId);
    }

    @Override
    public void validatePassword(String password) {
        log.debug("Validating password (length: {})", password != null ? password.length() : 0);

        if (password == null || password.trim().isEmpty()) {
            throw new UserExceptions.InvalidPasswordException("Password is required");
        }

        String cleanPassword = password.trim();
        int minLength = serviceProperties.getPasswordMinLength();

        if (cleanPassword.length() < minLength) {
            throw new UserExceptions.InvalidPasswordException(
                    "Password must be at least " + minLength + " characters long");
        }

        log.debug("Password validation successful");
    }

    @Override
    public void validateStudentId(String studentId) {
        log.debug("Validating student ID: {}", studentId);

        if (studentId == null || studentId.trim().isEmpty()) {
            throw new UserExceptions.InvalidUserDataException("Student ID is required");
        }

        String cleanStudentId = studentId.trim();

        if (!cleanStudentId.matches("^[0-9]{8,12}$")) {
            throw new UserExceptions.InvalidUserDataException("Student ID must be 8-12 digits");
        }

        boolean exists = studentRepository.findAll().stream()
                .anyMatch(student -> cleanStudentId.equals(student.getStudentID()));

        if (exists) {
            throw new UserExceptions.DuplicateStudentException(cleanStudentId);
        }

        log.debug("Student ID validation successful: {}", cleanStudentId);
    }
}