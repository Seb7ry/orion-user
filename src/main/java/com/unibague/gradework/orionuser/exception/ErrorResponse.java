package com.unibague.gradework.orionuser.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized error response for User Service
 * Provides consistent error information across all endpoints
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Error code identifying the type of error
     * Examples: "USER_NOT_FOUND", "VALIDATION_ERROR", "DUPLICATE_USER"
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Timestamp when the error occurred
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Request path where the error occurred
     */
    private String path;

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Service name that generated the error
     * Always "orion-user" for this service
     */
    private String service;
}