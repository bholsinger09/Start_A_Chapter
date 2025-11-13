package com.turningpoint.chapterorganizer.exception;

/**
 * Exception thrown when member authentication fails.
 * Named from the caller's perspective - the authentication process.
 */
public class MemberAuthenticationException extends RuntimeException {
    
    private final AuthenticationFailureReason reason;
    private final String userFriendlyMessage;
    
    public enum AuthenticationFailureReason {
        MEMBER_NOT_FOUND("No account found with the provided credentials"),
        INVALID_PASSWORD("Password is incorrect"),
        ACCOUNT_DISABLED("Account has been disabled"),
        MISSING_CREDENTIALS("Email or username and password are required"),
        INVALID_CREDENTIALS_FORMAT("Credentials format is invalid"),
        TOO_MANY_ATTEMPTS("Too many failed login attempts, account temporarily locked");
        
        private final String defaultMessage;
        
        AuthenticationFailureReason(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }
        
        public String getDefaultMessage() {
            return defaultMessage;
        }
    }
    
    public MemberAuthenticationException(AuthenticationFailureReason reason) {
        super(reason.getDefaultMessage());
        this.reason = reason;
        this.userFriendlyMessage = reason.getDefaultMessage();
    }
    
    public MemberAuthenticationException(AuthenticationFailureReason reason, String customMessage) {
        super(customMessage);
        this.reason = reason;
        this.userFriendlyMessage = customMessage;
    }
    
    public MemberAuthenticationException(AuthenticationFailureReason reason, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.reason = reason;
        this.userFriendlyMessage = customMessage;
    }
    
    public AuthenticationFailureReason getReason() {
        return reason;
    }
    
    public String getUserFriendlyMessage() {
        return userFriendlyMessage;
    }
    
    /**
     * Creates authentication exception for member not found
     */
    public static MemberAuthenticationException memberNotFound(String identifier) {
        return new MemberAuthenticationException(
            AuthenticationFailureReason.MEMBER_NOT_FOUND,
            "No account found for '" + identifier + "'"
        );
    }
    
    /**
     * Creates authentication exception for invalid password
     */
    public static MemberAuthenticationException invalidPassword() {
        return new MemberAuthenticationException(
            AuthenticationFailureReason.INVALID_PASSWORD
        );
    }
    
    /**
     * Creates authentication exception for missing credentials
     */
    public static MemberAuthenticationException missingCredentials() {
        return new MemberAuthenticationException(
            AuthenticationFailureReason.MISSING_CREDENTIALS
        );
    }
    
    /**
     * Creates authentication exception for disabled account
     */
    public static MemberAuthenticationException accountDisabled(String identifier) {
        return new MemberAuthenticationException(
            AuthenticationFailureReason.ACCOUNT_DISABLED,
            "Account '" + identifier + "' has been disabled"
        );
    }
}