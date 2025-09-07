package com.unibague.gradework.orionuser.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // User-specific exceptions
    @ExceptionHandler(UserExceptions.UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserExceptions.UserNotFoundException ex, WebRequest request) {
        log.warn("User not found: {}", ex.getMessage());
        return buildErrorResponse("USER_NOT_FOUND", ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserExceptions.StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(UserExceptions.StudentNotFoundException ex, WebRequest request) {
        log.warn("Student not found: {}", ex.getMessage());
        return buildErrorResponse("STUDENT_NOT_FOUND", ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserExceptions.ActorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleActorNotFound(UserExceptions.ActorNotFoundException ex, WebRequest request) {
        log.warn("Actor not found: {}", ex.getMessage());
        return buildErrorResponse("ACTOR_NOT_FOUND", ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserExceptions.RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(UserExceptions.RoleNotFoundException ex, WebRequest request) {
        log.warn("Role not found: {}", ex.getMessage());
        return buildErrorResponse("ROLE_NOT_FOUND", ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserExceptions.DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(UserExceptions.DuplicateUserException ex, WebRequest request) {
        log.warn("Duplicate user: {}", ex.getMessage());
        return buildErrorResponse("DUPLICATE_USER", ex.getMessage(), request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserExceptions.DuplicateStudentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateStudent(UserExceptions.DuplicateStudentException ex, WebRequest request) {
        log.warn("Duplicate student: {}", ex.getMessage());
        return buildErrorResponse("DUPLICATE_STUDENT", ex.getMessage(), request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserExceptions.InvalidUserDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserData(UserExceptions.InvalidUserDataException ex, WebRequest request) {
        log.warn("Invalid user data: {}", ex.getMessage());
        return buildErrorResponse("INVALID_USER_DATA", ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExceptions.InvalidEmailException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmail(UserExceptions.InvalidEmailException ex, WebRequest request) {
        log.warn("Invalid email: {}", ex.getMessage());
        return buildErrorResponse("INVALID_EMAIL", ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExceptions.InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(UserExceptions.InvalidPasswordException ex, WebRequest request) {
        log.warn("Invalid password: {}", ex.getMessage());
        return buildErrorResponse("INVALID_PASSWORD", ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExceptions.InsufficientPermissionsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientPermissions(UserExceptions.InsufficientPermissionsException ex, WebRequest request) {
        log.warn("Insufficient permissions: {}", ex.getMessage());
        return buildErrorResponse("INSUFFICIENT_PERMISSIONS", ex.getMessage(), request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserExceptions.ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(UserExceptions.ExternalServiceException ex, WebRequest request) {
        log.error("External service error: {}", ex.getMessage(), ex);
        return buildErrorResponse("EXTERNAL_SERVICE_ERROR", ex.getMessage(), request, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Validation exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(IllegalArgumentException ex, WebRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        return buildErrorResponse("VALIDATION_ERROR", ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Bean validation error: {}", message);
        return buildErrorResponse("VALIDATION_ERROR", "Validation failed: " + message, request, HttpStatus.BAD_REQUEST);
    }

    // Generic exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
            log.warn("Resource not found: {}", ex.getMessage());
            return buildErrorResponse("NOT_FOUND", ex.getMessage(), request, HttpStatus.NOT_FOUND);
        }

        log.error("Internal server error: {}", ex.getMessage(), ex);
        return buildErrorResponse("INTERNAL_ERROR", "An unexpected error occurred: " + ex.getMessage(),
                request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse("UNEXPECTED_ERROR", "Something went wrong. Please try again later.",
                request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method
    private ResponseEntity<ErrorResponse> buildErrorResponse(String errorCode, String message,
                                                             WebRequest request, HttpStatus status) {
        ErrorResponse error = ErrorResponse.builder()
                .error(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(status.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(status).body(error);
    }
}