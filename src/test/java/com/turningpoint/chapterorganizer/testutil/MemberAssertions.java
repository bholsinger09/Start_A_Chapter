package com.turningpoint.chapterorganizer.testutil;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import org.assertj.core.api.AbstractAssert;

/**
 * Custom Assertions for Member Domain Object
 * 
 * Following JUnit Internals best practices:
 * - Domain-specific assertion language
 * - Fluent interface for readable tests
 * - Encapsulated complex validation logic
 * - Clear error messages for failures
 */
public class MemberAssertions extends AbstractAssert<MemberAssertions, Member> {

    public MemberAssertions(Member actual) {
        super(actual, MemberAssertions.class);
    }
    
    /**
     * Static factory method following AssertJ conventions
     */
    public static MemberAssertions assertThat(Member actual) {
        return new MemberAssertions(actual);
    }
    
    /**
     * Validates that member is properly initialized
     */
    public MemberAssertions isValidMember() {
        isNotNull();
        
        if (actual.getFirstName() == null || actual.getFirstName().trim().isEmpty()) {
            failWithMessage("Expected member to have first name but was <%s>", actual.getFirstName());
        }
        
        if (actual.getLastName() == null || actual.getLastName().trim().isEmpty()) {
            failWithMessage("Expected member to have last name but was <%s>", actual.getLastName());
        }
        
        if (actual.getEmail() == null || !actual.getEmail().contains("@")) {
            failWithMessage("Expected member to have valid email but was <%s>", actual.getEmail());
        }
        
        if (actual.getChapter() == null) {
            failWithMessage("Expected member to be assigned to a chapter but was null");
        }
        
        return this;
    }
    
    /**
     * Validates full name matches expected value
     */
    public MemberAssertions hasFullName(String expectedFullName) {
        isNotNull();
        String actualFullName = actual.getFirstName() + " " + actual.getLastName();
        
        if (!actualFullName.equals(expectedFullName)) {
            failWithMessage("Expected member full name to be <%s> but was <%s>", 
                expectedFullName, actualFullName);
        }
        
        return this;
    }
    
    /**
     * Validates member has expected role
     */
    public MemberAssertions hasRole(MemberRole expectedRole) {
        isNotNull();
        
        if (actual.getRole() != expectedRole) {
            failWithMessage("Expected member role to be <%s> but was <%s>", 
                expectedRole, actual.getRole());
        }
        
        return this;
    }
    
    /**
     * Validates member is in expected chapter
     */
    public MemberAssertions isInChapter(String expectedChapterName) {
        isNotNull();
        
        if (actual.getChapter() == null) {
            failWithMessage("Expected member to be in chapter <%s> but chapter was null", 
                expectedChapterName);
        }
        
        if (!actual.getChapter().getName().equals(expectedChapterName)) {
            failWithMessage("Expected member to be in chapter <%s> but was in <%s>", 
                expectedChapterName, actual.getChapter().getName());
        }
        
        return this;
    }
    
    /**
     * Validates member has leadership role (President, VP, Secretary, Treasurer)
     */
    public MemberAssertions hasLeadershipRole() {
        isNotNull();
        
        MemberRole role = actual.getRole();
        if (role != MemberRole.PRESIDENT && 
            role != MemberRole.VICE_PRESIDENT && 
            role != MemberRole.SECRETARY && 
            role != MemberRole.TREASURER) {
            
            failWithMessage("Expected member to have leadership role but was <%s>", role);
        }
        
        return this;
    }
    
    /**
     * Validates member is active
     */
    public MemberAssertions isActive() {
        isNotNull();
        
        if (!actual.getActive()) {
            failWithMessage("Expected member to be active but was inactive");
        }
        
        return this;
    }
    
    /**
     * Validates member is inactive
     */
    public MemberAssertions isInactive() {
        isNotNull();
        
        if (actual.getActive()) {
            failWithMessage("Expected member to be inactive but was active");
        }
        
        return this;
    }
    
    /**
     * Validates member has expected email domain
     */
    public MemberAssertions hasEmailDomain(String expectedDomain) {
        isNotNull();
        
        if (actual.getEmail() == null) {
            failWithMessage("Expected member to have email with domain <%s> but email was null", 
                expectedDomain);
        }
        
        String[] emailParts = actual.getEmail().split("@");
        if (emailParts.length != 2 || !emailParts[1].equals(expectedDomain)) {
            failWithMessage("Expected member email domain to be <%s> but was <%s>", 
                expectedDomain, emailParts.length > 1 ? emailParts[1] : "invalid");
        }
        
        return this;
    }
    
    /**
     * Validates member has expected major
     */
    public MemberAssertions hasMajor(String expectedMajor) {
        isNotNull();
        
        if (!expectedMajor.equals(actual.getMajor())) {
            failWithMessage("Expected member major to be <%s> but was <%s>", 
                expectedMajor, actual.getMajor());
        }
        
        return this;
    }
    
    /**
     * Validates member is graduating in expected year
     */
    public MemberAssertions isGraduatingIn(String expectedYear) {
        isNotNull();
        
        if (!expectedYear.equals(actual.getGraduationYear())) {
            failWithMessage("Expected member graduation year to be <%s> but was <%s>", 
                expectedYear, actual.getGraduationYear());
        }
        
        return this;
    }
    
    /**
     * Validates member has complete contact information
     */
    public MemberAssertions hasCompleteContactInfo() {
        isNotNull();
        
        if (actual.getEmail() == null || actual.getEmail().trim().isEmpty()) {
            failWithMessage("Expected member to have email for complete contact info but was <%s>", 
                actual.getEmail());
        }
        
        if (actual.getPhoneNumber() == null || actual.getPhoneNumber().trim().isEmpty()) {
            failWithMessage("Expected member to have phone number for complete contact info but was <%s>", 
                actual.getPhoneNumber());
        }
        
        return this;
    }
    
    /**
     * Validates member ID is set (for persisted entities)
     */
    public MemberAssertions isPersisted() {
        isNotNull();
        
        if (actual.getId() == null) {
            failWithMessage("Expected member to be persisted (have ID) but ID was null");
        }
        
        return this;
    }
    
    /**
     * Validates member matches the pattern created by our refactored createMember helper
     */
    public MemberAssertions wasCreatedByRefactoredMethod() {
        isValidMember();
        hasCompleteContactInfo();
        
        // Additional validation that our refactored method sets all required fields
        if (actual.getUsername() == null || actual.getUsername().trim().isEmpty()) {
            failWithMessage("Expected member created by refactored method to have username but was <%s>", 
                actual.getUsername());
        }
        
        if (actual.getRole() == null) {
            failWithMessage("Expected member created by refactored method to have role but was null");
        }
        
        return this;
    }
}