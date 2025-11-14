package com.turningpoint.chapterorganizer.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception Handler Service
 * Fixes: Duplicated Code smell - Centralized exception handling logic
 * Single Responsibility: Handle all types of exceptions consistently
 */
@Service
public class ExceptionHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerService.class);
    private final ResponseService responseService;

    public ExceptionHandlerService(ResponseService responseService) {
        this.responseService = responseService;
    }

    /**
     * Handle generic exceptions.
     * Fixes: Duplicated exception handling across controllers
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        return responseService.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred. Please try again later.");
    }

    /**
     * Handle entity not found exceptions.
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleEntityNotFoundException(
            EntityNotFoundException ex) {
        logger.warn("Entity not found: {}", ex.getMessage());
        return responseService.notFound(ex.getMessage());
    }

    /**
     * Handle illegal argument exceptions.
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        return responseService.validationError(ex.getMessage());
    }

    /**
     * Handle validation errors from @Valid annotations.
     * Fixes: Duplicated validation error handling
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex) {
        logger.warn("Validation failed: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();
        
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        return responseService.error(HttpStatus.BAD_REQUEST, 
                "Validation failed: " + formatValidationErrors(errors));
    }

    /**
     * Handle authentication exceptions.
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleAuthenticationException(
            Exception ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        return responseService.unauthorized("Authentication required. Please login.");
    }

    /**
     * Handle authorization exceptions.
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleAuthorizationException(
            Exception ex) {
        logger.warn("Authorization failed: {}", ex.getMessage());
        return responseService.forbidden("Access denied. You do not have permission.");
    }

    /**
     * Handle data integrity violations.
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleDataIntegrityException(
            Exception ex) {
        logger.error("Data integrity violation", ex);
        
        String message = extractDataIntegrityMessage(ex.getMessage());
        return responseService.error(HttpStatus.CONFLICT, message);
    }

    /**
     * Handle resource already exists exceptions.
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleResourceExistsException(
            Exception ex) {
        logger.warn("Resource already exists: {}", ex.getMessage());
        return responseService.error(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handle timeout exceptions.
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleTimeoutException(
            Exception ex) {
        logger.error("Operation timed out", ex);
        return responseService.error(HttpStatus.REQUEST_TIMEOUT, 
                "Operation timed out. Please try again.");
    }

    /**
     * Handle rate limit exceptions.
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleRateLimitException(
            Exception ex) {
        logger.warn("Rate limit exceeded: {}", ex.getMessage());
        return responseService.error(HttpStatus.TOO_MANY_REQUESTS, 
                "Too many requests. Please try again later.");
    }

    /**
     * Create error response with exception details for debugging.
     * Should only be used in development environment.
     */
    public ResponseEntity<ResponseService.ApiResponse<Map<String, Object>>> handleExceptionWithDetails(
            Exception ex) {
        logger.error("Exception with details", ex);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("type", ex.getClass().getSimpleName());
        errorDetails.put("cause", ex.getCause() != null ? ex.getCause().getMessage() : null);
        
        ResponseService.ApiResponse<Map<String, Object>> response = 
                new ResponseService.ApiResponse<>(false, "Error occurred", errorDetails);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Log and handle critical system errors.
     */
    public ResponseEntity<ResponseService.ApiResponse<Object>> handleCriticalException(
            Exception ex) {
        logger.error("CRITICAL ERROR - System may be unstable", ex);
        
        // In production, this might trigger alerts or notifications
        return responseService.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                "A critical error occurred. System administrators have been notified.");
    }

    /**
     * Format validation errors for user-friendly display.
     * Fixes: Inconsistent error message formatting
     */
    private String formatValidationErrors(Map<String, String> errors) {
        if (errors.isEmpty()) {
            return "Validation failed";
        }
        
        StringBuilder sb = new StringBuilder();
        errors.forEach((field, message) -> {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(field).append(": ").append(message);
        });
        
        return sb.toString();
    }

    /**
     * Extract user-friendly message from data integrity exceptions.
     */
    private String extractDataIntegrityMessage(String technicalMessage) {
        if (technicalMessage == null) {
            return "Data integrity constraint violated";
        }
        
        // Common constraint violation patterns
        if (technicalMessage.toLowerCase().contains("unique")) {
            return "A record with this information already exists";
        }
        if (technicalMessage.toLowerCase().contains("foreign key")) {
            return "Cannot perform this operation due to related data dependencies";
        }
        if (technicalMessage.toLowerCase().contains("not null")) {
            return "Required information is missing";
        }
        
        return "Data integrity constraint violated";
    }

    /**
     * Determine if exception should be logged as error or warning.
     * Fixes: Inconsistent logging levels
     */
    public void logException(Exception ex, String context) {
        if (isCriticalException(ex)) {
            logger.error("Critical error in {}: {}", context, ex.getMessage(), ex);
        } else if (isUserException(ex)) {
            logger.warn("User error in {}: {}", context, ex.getMessage());
        } else {
            logger.info("Expected exception in {}: {}", context, ex.getMessage());
        }
    }

    /**
     * Check if exception is critical and requires immediate attention.
     */
    private boolean isCriticalException(Exception ex) {
        return ex instanceof RuntimeException && 
               (ex.getClass().getSimpleName().contains("OutOfMemory") ||
                ex.getClass().getSimpleName().contains("StackOverflow") ||
                (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("database connection")));
    }

    /**
     * Check if exception is caused by user input/behavior.
     */
    private boolean isUserException(Exception ex) {
        return ex instanceof IllegalArgumentException ||
               ex instanceof EntityNotFoundException ||
               ex instanceof MethodArgumentNotValidException;
    }
}