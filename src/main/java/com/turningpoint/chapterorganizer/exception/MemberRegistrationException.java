package com.turningpoint.chapterorganizer.exception;

/**
 * Exception thrown when member registration fails due to business rule violations
 * or data integrity issues. Named from the caller's perspective - the registration process.
 */
public class MemberRegistrationException extends RuntimeException {
    
    private final RegistrationFailureReason reason;
    private final String userFriendlyMessage;
    
    public enum RegistrationFailureReason {
        DUPLICATE_EMAIL("An account with this email already exists"),
        INVALID_EMAIL_FORMAT("Email address format is invalid"),
        WEAK_PASSWORD("Password does not meet security requirements"),
        MISSING_REQUIRED_FIELD("Required information is missing"),
        CHAPTER_NOT_FOUND("Selected chapter is not available"),
        UNIVERSITY_NOT_FOUND("University information is invalid"),
        DATA_INTEGRITY_VIOLATION("Registration conflicts with existing data");
        
        private final String defaultMessage;
        
        RegistrationFailureReason(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }
        
        public String getDefaultMessage() {
            return defaultMessage;
        }
    }
    
    public MemberRegistrationException(RegistrationFailureReason reason) {
        super(reason.getDefaultMessage());
        this.reason = reason;
        this.userFriendlyMessage = reason.getDefaultMessage();
    }
    
    public MemberRegistrationException(RegistrationFailureReason reason, String customMessage) {
        super(customMessage);
        this.reason = reason;
        this.userFriendlyMessage = customMessage;
    }
    
    public MemberRegistrationException(RegistrationFailureReason reason, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.reason = reason;
        this.userFriendlyMessage = customMessage;
    }
    
    public RegistrationFailureReason getReason() {
        return reason;
    }
    
    public String getUserFriendlyMessage() {
        return userFriendlyMessage;
    }
    
    /**
     * Creates a registration exception for duplicate email scenario
     */
    public static MemberRegistrationException duplicateEmail(String email) {
        return new MemberRegistrationException(
            RegistrationFailureReason.DUPLICATE_EMAIL,
            "An account with email '" + email + "' already exists"
        );
    }
    
    /**
     * Creates a registration exception for invalid email format
     */
    public static MemberRegistrationException invalidEmailFormat(String email) {
        return new MemberRegistrationException(
            RegistrationFailureReason.INVALID_EMAIL_FORMAT,
            "Email address '" + email + "' is not in a valid format"
        );
    }
    
    /**
     * Creates a registration exception for weak password
     */
    public static MemberRegistrationException weakPassword() {
        return new MemberRegistrationException(
            RegistrationFailureReason.WEAK_PASSWORD,
            "Password must be at least 6 characters long and contain both letters and numbers"
        );
    }
    
    /**
     * Creates a registration exception for missing required field
     */
    public static MemberRegistrationException missingRequiredField(String fieldName) {
        return new MemberRegistrationException(
            RegistrationFailureReason.MISSING_REQUIRED_FIELD,
            fieldName + " is required for registration"
        );
    }
}