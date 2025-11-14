package com.turningpoint.chapterorganizer.exception;

/**
 * Exception thrown when attempting to register with an email that already exists.
 * Provides better error handling and user experience.
 */
public class DuplicateEmailException extends RuntimeException {
    private final String email;

    public DuplicateEmailException(String email) {
        super("Member with this email already exists");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUserFriendlyMessage() {
        return "An account with this email address already exists. Please try logging in instead, or use a different email address.";
    }
}