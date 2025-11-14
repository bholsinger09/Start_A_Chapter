package com.turningpoint.chapterorganizer.util;

/**
 * Utility class for common validation operations.
 * Fixes: Duplicated Code smell across services and controllers.
 * Single Responsibility: Provide reusable validation methods.
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Check if a string is null, empty, or contains only whitespace.
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if a string is not null and not empty.
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Validate email format using basic regex.
     */
    public static boolean isValidEmail(String email) {
        if (isBlank(email)) {
            return false;
        }
        // Basic email validation - could use more sophisticated regex in production
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Validate ID is positive number.
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    /**
     * Validate password meets minimum requirements.
     */
    public static boolean isValidPassword(String password) {
        return isNotBlank(password) && password.length() >= 6;
    }

    /**
     * Validate phone number format.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (isBlank(phoneNumber)) {
            return true; // Phone number is optional
        }
        // Basic phone number validation - digits, spaces, hyphens, parentheses
        return phoneNumber.matches("^[\\d\\s\\-\\(\\)\\+]+$") && phoneNumber.length() >= 10;
    }

    /**
     * Validate name contains only letters, spaces, and common punctuation.
     */
    public static boolean isValidName(String name) {
        if (isBlank(name)) {
            return false;
        }
        // Allow letters, spaces, apostrophes, hyphens
        return name.matches("^[a-zA-Z\\s'\\-]+$") && name.length() <= 50;
    }

    /**
     * Require non-null argument with custom message.
     */
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
        return obj;
    }

    /**
     * Require non-blank string with custom message.
     */
    public static String requireNonBlank(String str, String message) {
        if (isBlank(str)) {
            throw new IllegalArgumentException(message);
        }
        return str.trim();
    }
}