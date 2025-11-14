package com.turningpoint.chapterorganizer.service.constants;

import com.turningpoint.chapterorganizer.entity.MemberRole;

/**
 * Member Service Constants
 * Boy Scout Rule: Extract magic values and hard-coded strings for better maintainability
 * Heuristic: Avoid Hard-Coding Values - Centralize business logic constants
 */
public final class MemberServiceConstants {
    
    private MemberServiceConstants() {
        // Utility class - prevent instantiation
    }
    
    // Default Values - Boy Scout Rule: Clear default business logic
    public static final boolean DEFAULT_MEMBER_ACTIVE_STATUS = true;
    public static final MemberRole DEFAULT_MEMBER_ROLE = MemberRole.MEMBER;
    
    // Database Constraint Keys - Boy Scout Rule: Centralize constraint handling
    public static final String EMAIL_CONSTRAINT_KEY = "uk_member_email";
    public static final String EMAIL_CONSTRAINT_KEYWORD = "email";
    public static final String USERNAME_CONSTRAINT_KEY = "uk_member_username";  
    public static final String USERNAME_CONSTRAINT_KEYWORD = "username";
    public static final String UNIQUE_CONSTRAINT_KEYWORD = "unique constraint";
    public static final String DUPLICATE_CONSTRAINT_KEYWORD = "duplicate";
    
    // Error Messages - Boy Scout Rule: Consistent error messaging
    public static final String CHAPTER_NOT_FOUND_ERROR = "Chapter not found with id: %d";
    public static final String EMAIL_ALREADY_EXISTS_ERROR = "Member with this email already exists";
    public static final String USERNAME_ALREADY_EXISTS_ERROR = "Member with this username already exists";
    public static final String MEMBER_ALREADY_EXISTS_ERROR = "Member with this information already exists";
    public static final String MEMBER_NOT_FOUND_ERROR = "Member not found with id: %d";
    public static final String DATA_CONFLICT_ERROR = "Unable to create member due to data conflict: %s";
    public static final String INVALID_SEARCH_CRITERIA_ERROR = "Invalid search criteria provided";
    
    // Validation Messages - Boy Scout Rule: Centralized validation logic
    public static final String INVALID_EMAIL_FORMAT_ERROR = "Invalid email format provided";
    public static final String INVALID_MEMBER_DATA_ERROR = "Invalid member data provided";
    public static final String MEMBER_ID_REQUIRED_ERROR = "Member ID is required";
    public static final String EMAIL_REQUIRED_ERROR = "Email is required";
    
    // Business Logic Constants - Boy Scout Rule: Extract business rules
    public static final int MINIMUM_PASSWORD_LENGTH = 6;
    public static final int MAXIMUM_NAME_LENGTH = 50;
    public static final int MAXIMUM_EMAIL_LENGTH = 100;
    public static final int MAXIMUM_USERNAME_LENGTH = 30;
    
    // Performance Constants - Boy Scout Rule: Configurable performance settings
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAXIMUM_PAGE_SIZE = 100;
    public static final int SEARCH_RESULTS_LIMIT = 50;
    
    // Officer Role Validation - Boy Scout Rule: Business rule centralization
    public static final MemberRole[] OFFICER_ROLES = {
        MemberRole.PRESIDENT,
        MemberRole.VICE_PRESIDENT, 
        MemberRole.SECRETARY,
        MemberRole.TREASURER,
        MemberRole.OFFICER
    };
    
    /**
     * Check if a role is an officer role.
     * Boy Scout Rule: Encapsulate business logic in methods
     */
    public static boolean isOfficerRole(MemberRole role) {
        if (role == null) return false;
        
        for (MemberRole officerRole : OFFICER_ROLES) {
            if (officerRole.equals(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Format error message with parameters.
     * Boy Scout Rule: Reusable utility method
     */
    public static String formatErrorMessage(String template, Object... args) {
        return String.format(template, args);
    }
}