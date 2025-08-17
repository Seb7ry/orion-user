package com.unibague.gradework.orionserver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidationError (IllegalArgumentException ex, WebRequest request) {
        log.warn("Validation error: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("VALIDATION_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=",""))
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleNotFound (RuntimeException ex, WebRequest request) {
        if (ex.getMessage().toLowerCase().contains("not found")) {
            log.warn("Resource not found: {}", ex.getMessage());

            ErrorResponse error = ErrorResponse.builder()
                    .error("NOT_FOUND")
                    .message(ex.getMessage())
                    .timestamp(LocalDateTime.now())
                    .path(request.getDescription(false).replace("uri=", ""))
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        log.error("Internal server error: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .error("INTERNAL_ERROR")
                .message("An unexpected error occurred: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=",""))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex, WebRequest request){
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .error("UNEXPECTED_ERROR")
                .message("Somenthing went wrong. Please try again later.")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
