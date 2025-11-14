package com.turningpoint.chapterorganizer.exception;

/**
 * Exception thrown when attempting to register with a username that already exists.
 * Provides better error handling and user experience for username conflicts.
 */
public class DuplicateUsernameException extends RuntimeException {
    private final String username;

    public DuplicateUsernameException(String username) {
        super("Username already exists");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getUserFriendlyMessage() {
        return String.format("The username '%s' is already taken. Please choose a different email address or contact support if this is your account.", username);
    }
}