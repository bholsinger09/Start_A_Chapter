package com.turningpoint.chapterorganizer.security.exception;

/**
 * Exception thrown for security-related validation errors.
 */
public class SecurityException extends RuntimeException {
    
    public SecurityException(String message) {
        super(message);
    }
    
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}