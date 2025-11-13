package com.turningpoint.chapterorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.HashMap;

/**
 * Global exception handler that transforms internal exceptions into 
 * user-friendly HTTP responses. Implements try-catch-finally first approach
 * by defining error boundaries at the controller layer.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles member registration failures with appropriate HTTP status and user-friendly messages
     */
    @ExceptionHandler(MemberRegistrationException.class)
    public ResponseEntity<Map<String, Object>> handleMemberRegistrationException(MemberRegistrationException ex) {
        Map<String, Object> errorResponse = null;
        
        try {
            errorResponse = createErrorResponse(
                false,
                ex.getUserFriendlyMessage(),
                ex.getReason().name(),
                "REGISTRATION_FAILED"
            );
            
            // Map specific registration failures to appropriate HTTP status codes
            HttpStatus status = switch (ex.getReason()) {
                case DUPLICATE_EMAIL, DATA_INTEGRITY_VIOLATION -> HttpStatus.CONFLICT;
                case INVALID_EMAIL_FORMAT, WEAK_PASSWORD, MISSING_REQUIRED_FIELD -> HttpStatus.BAD_REQUEST;
                case CHAPTER_NOT_FOUND, UNIVERSITY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            };
            
            return ResponseEntity.status(status).body(errorResponse);
            
        } catch (Exception handlingError) {
            // Fallback error response if error handling itself fails
            return createFallbackErrorResponse("Registration failed due to internal error");
        } finally {
            // Log the registration failure for monitoring (in production, use proper logging)
            logRegistrationFailure(ex);
        }
    }
    
    /**
     * Handles member authentication failures with security-conscious responses
     */
    @ExceptionHandler(MemberAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleMemberAuthenticationException(MemberAuthenticationException ex) {
        Map<String, Object> errorResponse = null;
        
        try {
            // For security, don't expose specific authentication failure reasons to client
            String userMessage = switch (ex.getReason()) {
                case MEMBER_NOT_FOUND, INVALID_PASSWORD -> "Invalid email or password";
                case MISSING_CREDENTIALS -> "Email and password are required";
                case ACCOUNT_DISABLED -> "Account is currently disabled";
                case TOO_MANY_ATTEMPTS -> "Account temporarily locked due to too many failed attempts";
                default -> "Authentication failed";
            };
            
            errorResponse = createErrorResponse(
                false,
                userMessage,
                "AUTH_FAILED", // Don't expose specific internal reason
                "AUTHENTICATION_FAILED"
            );
            
            HttpStatus status = switch (ex.getReason()) {
                case MISSING_CREDENTIALS -> HttpStatus.BAD_REQUEST;
                case TOO_MANY_ATTEMPTS -> HttpStatus.TOO_MANY_REQUESTS;
                case ACCOUNT_DISABLED -> HttpStatus.FORBIDDEN;
                default -> HttpStatus.UNAUTHORIZED;
            };
            
            return ResponseEntity.status(status).body(errorResponse);
            
        } catch (Exception handlingError) {
            return createFallbackErrorResponse("Authentication failed");
        } finally {
            logAuthenticationFailure(ex);
        }
    }
    
    /**
     * Handles chapter management failures
     */
    @ExceptionHandler(ChapterManagementException.class)
    public ResponseEntity<Map<String, Object>> handleChapterManagementException(ChapterManagementException ex) {
        Map<String, Object> errorResponse = null;
        
        try {
            errorResponse = createErrorResponse(
                false,
                ex.getUserFriendlyMessage(),
                ex.getReason().name(),
                "CHAPTER_OPERATION_FAILED"
            );
            
            HttpStatus status = switch (ex.getReason()) {
                case CHAPTER_NOT_FOUND, UNIVERSITY_NOT_FOUND -> HttpStatus.NOT_FOUND;
                case DUPLICATE_CHAPTER -> HttpStatus.CONFLICT;
                case CHAPTER_HAS_MEMBERS -> HttpStatus.CONFLICT;
                case INVALID_CHAPTER_DATA -> HttpStatus.BAD_REQUEST;
                case CHAPTER_INACTIVE -> HttpStatus.FORBIDDEN;
            };
            
            return ResponseEntity.status(status).body(errorResponse);
            
        } catch (Exception handlingError) {
            return createFallbackErrorResponse("Chapter operation failed");
        } finally {
            logChapterOperationFailure(ex);
        }
    }
    
    /**
     * Handles data validation failures
     */
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDataValidationException(DataValidationException ex) {
        Map<String, Object> errorResponse = null;
        
        try {
            errorResponse = createErrorResponse(
                false,
                ex.getUserFriendlyMessage(),
                ex.getReason().name(),
                "VALIDATION_FAILED"
            );
            
            // Add specific field information for client-side handling
            errorResponse.put("field", ex.getFieldName());
            if (ex.getInvalidValue() != null) {
                errorResponse.put("invalidValue", ex.getInvalidValue());
            }
            
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception handlingError) {
            return createFallbackErrorResponse("Data validation failed");
        } finally {
            logValidationFailure(ex);
        }
    }
    
    /**
     * Handles unexpected exceptions with generic response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = null;
        
        try {
            // Don't expose internal error details to client
            errorResponse = createErrorResponse(
                false,
                "An unexpected error occurred. Please try again.",
                "INTERNAL_ERROR",
                "SYSTEM_ERROR"
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            
        } catch (Exception handlingError) {
            return createFallbackErrorResponse("System error occurred");
        } finally {
            logUnexpectedError(ex);
        }
    }
    
    /**
     * Creates standardized error response structure
     */
    private Map<String, Object> createErrorResponse(boolean success, String message, String errorCode, String errorType) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("error", message); // Backward compatibility
        response.put("errorCode", errorCode);
        response.put("errorType", errorType);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    /**
     * Creates fallback error response when error handling fails
     */
    private ResponseEntity<Map<String, Object>> createFallbackErrorResponse(String message) {
        Map<String, Object> response = Map.of(
            "success", false,
            "message", message,
            "error", message,
            "errorType", "SYSTEM_ERROR",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    // Logging methods (in production, use proper logging framework)
    private void logRegistrationFailure(MemberRegistrationException ex) {
        System.err.println("Registration failure: " + ex.getReason() + " - " + ex.getMessage());
    }
    
    private void logAuthenticationFailure(MemberAuthenticationException ex) {
        System.err.println("Authentication failure: " + ex.getReason() + " - " + ex.getMessage());
    }
    
    private void logChapterOperationFailure(ChapterManagementException ex) {
        System.err.println("Chapter operation failure: " + ex.getReason() + " - " + ex.getMessage());
    }
    
    private void logValidationFailure(DataValidationException ex) {
        System.err.println("Validation failure: " + ex.getFieldName() + " - " + ex.getMessage());
    }
    
    private void logUnexpectedError(Exception ex) {
        System.err.println("Unexpected error: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        ex.printStackTrace();
    }
}