package com.unibague.gradework.orionuser.service;

import com.unibague.gradework.orionuser.model.Role;

/**
 * Interface for user validation services
 * Provides centralized validation logic for user-related operations
 */
public interface IValidationService {

    /**
     * Validates that a role exists and is valid
     * @param role the role to validate
     * @return the validated role from database
     * @throws UserExceptions.RoleNotFoundException if role not found
     * @throws UserExceptions.InvalidUserDataException if role data is invalid
     */
    Role validateRole(Role role);

    /**
     * Validates email format and uniqueness for new users
     * @param email the email to validate
     * @throws UserExceptions.InvalidEmailException if email format/domain is invalid
     * @throws UserExceptions.DuplicateUserException if email already exists
     */
    void validateEmail(String email);

    /**
     * Validates email for user updates (allows same email)
     * @param existingEmail current user email
     * @param newEmail new email to validate
     * @throws UserExceptions.InvalidEmailException if email format/domain is invalid
     * @throws UserExceptions.DuplicateUserException if new email already exists
     */
    void validateEmailOnUpdate(String existingEmail, String newEmail);

    /**
     * Validates user ID is present and unique
     * @param idUser the user ID to validate
     * @throws UserExceptions.InvalidUserDataException if ID is invalid
     * @throws UserExceptions.DuplicateUserException if ID already exists
     */
    void validateIdUser(String idUser);

    /**
     * Validates password meets minimum requirements
     * @param password the password to validate
     * @throws UserExceptions.InvalidPasswordException if password is invalid
     */
    void validatePassword(String password);

    /**
     * Validates student ID format and uniqueness
     * @param studentId the student ID to validate
     * @throws UserExceptions.InvalidUserDataException if format is invalid
     * @throws UserExceptions.DuplicateStudentException if ID already exists
     */
    void validateStudentId(String studentId);
}