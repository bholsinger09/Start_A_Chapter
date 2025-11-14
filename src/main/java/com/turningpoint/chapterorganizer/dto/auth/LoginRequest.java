package com.turningpoint.chapterorganizer.dto.auth;

/**
 * Parameter object for login operations.
 * Fixes: Long Parameter Lists code smell.
 */
public class LoginRequest {
    private String email;
    private String password;
    
    // Default constructor
    public LoginRequest() {}
    
    // Constructor
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Validation helper
    public boolean isValid() {
        return email != null && !email.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
}