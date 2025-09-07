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

/**
 * Global exception handler for User Service
 * Provides consistent error responses and logging across all endpoints
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==========================================
    // USER-SPECIFIC EXCEPTIONS
    // ==========================================

    @ExceptionHandler(UserExceptions.UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserExceptions.UserNotFoundException ex, WebRequest request) {
        log.warn("User not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("USER_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.NOT_FOUND.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserExceptions.StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(UserExceptions.StudentNotFoundException ex, WebRequest request) {
        log.warn("Student not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("STUDENT_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.NOT_FOUND.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserExceptions.ActorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleActorNotFound(UserExceptions.ActorNotFoundException ex, WebRequest request) {
        log.warn("Actor not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("ACTOR_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.NOT_FOUND.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserExceptions.RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(UserExceptions.RoleNotFoundException ex, WebRequest request) {
        log.warn("Role not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("ROLE_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.NOT_FOUND.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserExceptions.DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(UserExceptions.DuplicateUserException ex, WebRequest request) {
        log.warn("Duplicate user: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("DUPLICATE_USER")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.CONFLICT.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserExceptions.DuplicateStudentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateStudent(UserExceptions.DuplicateStudentException ex, WebRequest request) {
        log.warn("Duplicate student: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("DUPLICATE_STUDENT")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.CONFLICT.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserExceptions.InvalidUserDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserData(UserExceptions.InvalidUserDataException ex, WebRequest request) {
        log.warn("Invalid user data: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("INVALID_USER_DATA")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .service("orion-user")
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserExceptions.InvalidEmailException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmail(UserExceptions.InvalidEmailException ex, WebRequest request) {
        log.warn("Invalid email: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("INVALID_EMAIL")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .service("orion-user")
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserExceptions.InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(UserExceptions.InvalidPasswordException ex, WebRequest request) {
        log.warn("Invalid password: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("INVALID_PASSWORD")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .service("orion-user")
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserExceptions.InsufficientPermissionsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientPermissions(UserExceptions.InsufficientPermissionsException ex, WebRequest request) {
        log.warn("Insufficient permissions: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("INSUFFICIENT_PERMISSIONS")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.FORBIDDEN.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(UserExceptions.ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(UserExceptions.ExternalServiceException ex, WebRequest request) {
        log.error("External service error: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .error("EXTERNAL_SERVICE_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    // ==========================================
    // VALIDATION EXCEPTIONS
    // ==========================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(IllegalArgumentException ex, WebRequest request) {
        log.warn("Validation error: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("VALIDATION_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .service("orion-user")
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Bean validation error: {}", message);

        ErrorResponse error = ErrorResponse.builder()
                .error("VALIDATION_ERROR")
                .message("Validation failed: " + message)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .service("orion-user")
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    // ==========================================
    // GENERIC EXCEPTIONS
    // ==========================================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
            log.warn("Resource not found: {}", ex.getMessage());

            ErrorResponse error = ErrorResponse.builder()
                    .error("NOT_FOUND")
                    .message(ex.getMessage())
                    .timestamp(LocalDateTime.now())
                    .path(request.getDescription(false).replace("uri=", ""))
                    .status(HttpStatus.NOT_FOUND.value())
                    .service("orion-user")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        log.error("Internal server error: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .error("INTERNAL_ERROR")
                .message("An unexpected error occurred: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .error("UNEXPECTED_ERROR")
                .message("Something went wrong. Please try again later.")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .service("orion-user")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}