package com.turningpoint.chapterorganizer.exception;

/**
 * Base exception for data validation failures.
 * Named from the caller's perspective - data validation operations.
 */
public class DataValidationException extends RuntimeException {
    
    private final ValidationFailureReason reason;
    private final String fieldName;
    private final String invalidValue;
    private final String userFriendlyMessage;
    
    public enum ValidationFailureReason {
        REQUIRED_FIELD_MISSING("Required field is missing"),
        INVALID_FORMAT("Field format is invalid"),
        VALUE_OUT_OF_RANGE("Field value is out of acceptable range"),
        INVALID_LENGTH("Field length is invalid"),
        INVALID_CHARACTERS("Field contains invalid characters"),
        BUSINESS_RULE_VIOLATION("Field value violates business rules");
        
        private final String defaultMessage;
        
        ValidationFailureReason(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }
        
        public String getDefaultMessage() {
            return defaultMessage;
        }
    }
    
    public DataValidationException(ValidationFailureReason reason, String fieldName) {
        super(reason.getDefaultMessage() + ": " + fieldName);
        this.reason = reason;
        this.fieldName = fieldName;
        this.invalidValue = null;
        this.userFriendlyMessage = reason.getDefaultMessage() + " for " + fieldName;
    }
    
    public DataValidationException(ValidationFailureReason reason, String fieldName, String invalidValue) {
        super(reason.getDefaultMessage() + ": " + fieldName + " = '" + invalidValue + "'");
        this.reason = reason;
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.userFriendlyMessage = fieldName + " value '" + invalidValue + "' is invalid";
    }
    
    public DataValidationException(ValidationFailureReason reason, String fieldName, String invalidValue, String customMessage) {
        super(customMessage);
        this.reason = reason;
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.userFriendlyMessage = customMessage;
    }
    
    public ValidationFailureReason getReason() {
        return reason;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public String getInvalidValue() {
        return invalidValue;
    }
    
    public String getUserFriendlyMessage() {
        return userFriendlyMessage;
    }
    
    /**
     * Creates validation exception for required field missing
     */
    public static DataValidationException requiredFieldMissing(String fieldName) {
        return new DataValidationException(
            ValidationFailureReason.REQUIRED_FIELD_MISSING,
            fieldName
        );
    }
    
    /**
     * Creates validation exception for invalid format
     */
    public static DataValidationException invalidFormat(String fieldName, String value, String expectedFormat) {
        return new DataValidationException(
            ValidationFailureReason.INVALID_FORMAT,
            fieldName,
            value,
            fieldName + " must be in format: " + expectedFormat
        );
    }
    
    /**
     * Creates validation exception for invalid length
     */
    public static DataValidationException invalidLength(String fieldName, int actualLength, int minLength, Integer maxLength) {
        String message = fieldName + " length (" + actualLength + ") must be ";
        if (maxLength != null) {
            message += "between " + minLength + " and " + maxLength + " characters";
        } else {
            message += "at least " + minLength + " characters";
        }
        
        return new DataValidationException(
            ValidationFailureReason.INVALID_LENGTH,
            fieldName,
            "length=" + actualLength,
            message
        );
    }
}