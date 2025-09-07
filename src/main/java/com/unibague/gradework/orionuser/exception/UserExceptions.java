package com.unibague.gradework.orionuser.exception;

/**
 * Custom exceptions for User Service domain
 * Provides specific exception types for user-related operations
 */
public class UserExceptions {

    /**
     * Exception thrown when a user is not found
     */
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String userId) {
            super("User not found with ID: " + userId);
        }

        public UserNotFoundException(String field, String value) {
            super("User not found with " + field + ": " + value);
        }
    }

    /**
     * Exception thrown when a student is not found
     */
    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String studentId) {
            super("Student not found with ID: " + studentId);
        }

        public StudentNotFoundException(String field, String value) {
            super("Student not found with " + field + ": " + value);
        }
    }

    /**
     * Exception thrown when an actor is not found
     */
    public static class ActorNotFoundException extends RuntimeException {
        public ActorNotFoundException(String actorId) {
            super("Actor not found with ID: " + actorId);
        }

        public ActorNotFoundException(String field, String value) {
            super("Actor not found with " + field + ": " + value);
        }
    }

    /**
     * Exception thrown when a role is not found
     */
    public static class RoleNotFoundException extends RuntimeException {
        public RoleNotFoundException(String roleId) {
            super("Role not found with ID: " + roleId);
        }

        public RoleNotFoundException(String field, String value) {
            super("Role not found with " + field + ": " + value);
        }
    }

    /**
     * Exception thrown when trying to create a duplicate user
     */
    public static class DuplicateUserException extends RuntimeException {
        public DuplicateUserException(String email) {
            super("User with email '" + email + "' already exists");
        }

        public DuplicateUserException(String field, String value) {
            super("User with " + field + " '" + value + "' already exists");
        }
    }

    /**
     * Exception thrown when trying to create a duplicate student ID
     */
    public static class DuplicateStudentException extends RuntimeException {
        public DuplicateStudentException(String studentId) {
            super("Student with ID '" + studentId + "' already exists");
        }
    }

    /**
     * Exception thrown for invalid user data
     */
    public static class InvalidUserDataException extends RuntimeException {
        public InvalidUserDataException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown for invalid email format or domain
     */
    public static class InvalidEmailException extends RuntimeException {
        public InvalidEmailException(String email) {
            super("Invalid email format or domain: " + email);
        }

        public InvalidEmailException(String email, String expectedDomain) {
            super("Email '" + email + "' does not belong to expected domain: " + expectedDomain);
        }
    }

    /**
     * Exception thrown for invalid password
     */
    public static class InvalidPasswordException extends RuntimeException {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when user lacks required permissions
     */
    public static class InsufficientPermissionsException extends RuntimeException {
        public InsufficientPermissionsException(String action) {
            super("Insufficient permissions to perform action: " + action);
        }

        public InsufficientPermissionsException(String userId, String action) {
            super("User " + userId + " lacks permissions to perform action: " + action);
        }
    }

    /**
     * Exception thrown when external service (like Program Service) fails
     */
    public static class ExternalServiceException extends RuntimeException {
        public ExternalServiceException(String serviceName, String message) {
            super("Error communicating with " + serviceName + ": " + message);
        }

        public ExternalServiceException(String serviceName, Throwable cause) {
            super("Error communicating with " + serviceName + ": " + cause.getMessage(), cause);
        }
    }
}