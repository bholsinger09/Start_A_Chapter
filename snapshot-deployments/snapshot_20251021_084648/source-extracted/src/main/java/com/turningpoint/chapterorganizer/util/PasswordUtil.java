package com.turningpoint.chapterorganizer.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Simple password encoder utility
 */
public class PasswordUtil {
    
    /**
     * Encode a password using SHA-256 (for development purposes)
     * In production, use BCrypt or similar strong hashing algorithm
     */
    public static String encode(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }
    
    /**
     * Check if a password matches the encoded password
     */
    public static boolean matches(String password, String encodedPassword) {
        return encode(password).equals(encodedPassword);
    }
}