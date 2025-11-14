package com.turningpoint.chapterorganizer.repository.constants;

/**
 * Repository Query Constants
 * Boy Scout Rule: Extract magic strings and hard-coded values to named constants
 * Heuristic: Avoid Hard-Coding Values - Use named constants for better maintainability
 */
public final class MemberQueryConstants {
    
    private MemberQueryConstants() {
        // Utility class - prevent instantiation
    }
    
    // Query Parameter Names - Clear naming for better readability
    public static final String CHAPTER_ID_PARAM = "chapterId";
    public static final String MEMBER_ID_PARAM = "id";
    public static final String EMAIL_PARAM = "email";
    public static final String ROLE_PARAM = "role";
    public static final String FIRST_NAME_PARAM = "firstName";
    public static final String LAST_NAME_PARAM = "lastName";
    public static final String MAJOR_PARAM = "major";
    public static final String GRADUATION_YEAR_PARAM = "graduationYear";
    public static final String ACTIVE_PARAM = "active";
    
    // Query Conditions - Single source of truth for query logic
    public static final String ACTIVE_CONDITION = "m.active = true";
    public static final String CHAPTER_MATCH_CONDITION = "m.chapter.id = :chapterId";
    public static final String ROLE_MATCH_CONDITION = "m.role = :role";
    
    // Officer Roles Query - Centralized role definition
    public static final String OFFICER_ROLES = "'PRESIDENT', 'VICE_PRESIDENT', 'SECRETARY', 'TREASURER', 'OFFICER'";
    public static final String OFFICER_ROLES_CONDITION = "m.role IN (" + OFFICER_ROLES + ")";
    
    // Search Pattern for LIKE queries - Consistent search formatting
    public static final String WILDCARD_PATTERN = "CONCAT('%', :%s, '%')";
    public static final String CASE_INSENSITIVE_LIKE = "LOWER(m.%s) LIKE LOWER(" + WILDCARD_PATTERN + ")";
    
    // Common Query Fragments - Reusable query parts
    public static final String SELECT_MEMBER = "SELECT m FROM Member m";
    public static final String COUNT_MEMBER = "SELECT COUNT(m) FROM Member m";
    public static final String SELECT_DISTINCT_MEMBER = "SELECT DISTINCT m FROM Member m";
    
    // Join Conditions - Prevent N+1 queries with explicit joins
    public static final String LEFT_JOIN_CHAPTER = "LEFT JOIN FETCH m.chapter";
    
    // Order By Clauses - Consistent ordering
    public static final String ORDER_BY_ROLE = "ORDER BY m.role";
    public static final String ORDER_BY_NAME = "ORDER BY m.lastName, m.firstName";
}