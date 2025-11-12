package com.turningpoint.chapterorganizer.testutil;

import com.turningpoint.chapterorganizer.entity.Chapter;
import org.assertj.core.api.AbstractAssert;

/**
 * Custom Assertions for Chapter Domain Object
 * 
 * Following JUnit Internals best practices:
 * - Domain-specific assertion language
 * - Fluent interface for readable tests
 * - Encapsulated complex validation logic
 * - Clear error messages for failures
 */
public class ChapterAssertions extends AbstractAssert<ChapterAssertions, Chapter> {

    public ChapterAssertions(Chapter actual) {
        super(actual, ChapterAssertions.class);
    }
    
    /**
     * Static factory method following AssertJ conventions
     */
    public static ChapterAssertions assertThat(Chapter actual) {
        return new ChapterAssertions(actual);
    }
    
    /**
     * Validates that chapter is properly initialized
     */
    public ChapterAssertions isValidChapter() {
        isNotNull();
        
        if (actual.getName() == null || actual.getName().trim().isEmpty()) {
            failWithMessage("Expected chapter to have name but was <%s>", actual.getName());
        }
        
        if (actual.getUniversityName() == null || actual.getUniversityName().trim().isEmpty()) {
            failWithMessage("Expected chapter to have university name but was <%s>", 
                actual.getUniversityName());
        }
        
        if (actual.getCity() == null || actual.getCity().trim().isEmpty()) {
            failWithMessage("Expected chapter to have city but was <%s>", actual.getCity());
        }
        
        if (actual.getState() == null || actual.getState().trim().isEmpty()) {
            failWithMessage("Expected chapter to have state but was <%s>", actual.getState());
        }
        
        return this;
    }
    
    /**
     * Validates chapter has expected name
     */
    public ChapterAssertions hasName(String expectedName) {
        isNotNull();
        
        if (!expectedName.equals(actual.getName())) {
            failWithMessage("Expected chapter name to be <%s> but was <%s>", 
                expectedName, actual.getName());
        }
        
        return this;
    }
    
    /**
     * Validates chapter is at expected university
     */
    public ChapterAssertions isAtUniversity(String expectedUniversity) {
        isNotNull();
        
        if (!expectedUniversity.equals(actual.getUniversityName())) {
            failWithMessage("Expected chapter to be at university <%s> but was <%s>", 
                expectedUniversity, actual.getUniversityName());
        }
        
        return this;
    }
    
    /**
     * Validates chapter is in expected location
     */
    public ChapterAssertions isInLocation(String expectedCity, String expectedState) {
        isNotNull();
        
        if (!expectedCity.equals(actual.getCity())) {
            failWithMessage("Expected chapter city to be <%s> but was <%s>", 
                expectedCity, actual.getCity());
        }
        
        if (!expectedState.equals(actual.getState())) {
            failWithMessage("Expected chapter state to be <%s> but was <%s>", 
                expectedState, actual.getState());
        }
        
        return this;
    }
    
    /**
     * Validates chapter is in California
     */
    public ChapterAssertions isInCalifornia() {
        return isInState("CA");
    }
    
    /**
     * Validates chapter is in expected state
     */
    public ChapterAssertions isInState(String expectedState) {
        isNotNull();
        
        if (!expectedState.equals(actual.getState())) {
            failWithMessage("Expected chapter to be in state <%s> but was <%s>", 
                expectedState, actual.getState());
        }
        
        return this;
    }
    
    /**
     * Validates chapter is active
     */
    public ChapterAssertions isActive() {
        isNotNull();
        
        if (!actual.getActive()) {
            failWithMessage("Expected chapter to be active but was inactive");
        }
        
        return this;
    }
    
    /**
     * Validates chapter is inactive
     */
    public ChapterAssertions isInactive() {
        isNotNull();
        
        if (actual.getActive()) {
            failWithMessage("Expected chapter to be inactive but was active");
        }
        
        return this;
    }
    
    /**
     * Validates chapter ID is set (for persisted entities)
     */
    public ChapterAssertions isPersisted() {
        isNotNull();
        
        if (actual.getId() == null) {
            failWithMessage("Expected chapter to be persisted (have ID) but ID was null");
        }
        
        return this;
    }
    
    /**
     * Validates chapter has expected ID
     */
    public ChapterAssertions hasId(Long expectedId) {
        isNotNull();
        
        if (!expectedId.equals(actual.getId())) {
            failWithMessage("Expected chapter ID to be <%s> but was <%s>", 
                expectedId, actual.getId());
        }
        
        return this;
    }
    
    /**
     * Validates chapter name follows expected naming convention
     */
    public ChapterAssertions followsNamingConvention() {
        isNotNull();
        
        String name = actual.getName();
        if (name == null) {
            failWithMessage("Expected chapter name to follow convention but was null");
            return this;
        }
        
        if (name.trim().isEmpty()) {
            failWithMessage("Expected chapter name to follow convention but was empty");
            return this;
        }
        
        // Name should not have excessive whitespace or special characters
        if (!name.equals(name.trim())) {
            failWithMessage("Expected chapter name to be trimmed but had extra whitespace: <%s>", 
                name);
        }
        
        return this;
    }
    
    /**
     * Validates chapter matches one of our test universities (UCLA, Stanford, etc.)
     */
    public ChapterAssertions isTestUniversity() {
        isNotNull();
        
        String[] testUniversities = {
            "University of California, Los Angeles",
            "Stanford University", 
            "University of Southern California",
            "University of California, Berkeley",
            "Harvard University",
            "New York University"
        };
        
        String actualUniversity = actual.getUniversityName();
        for (String testUniversity : testUniversities) {
            if (testUniversity.equals(actualUniversity)) {
                return this;
            }
        }
        
        failWithMessage("Expected chapter to be at test university but was <%s>", actualUniversity);
        return this;
    }
    
    /**
     * Validates chapter was created by our refactored DataPopulation method
     */
    public ChapterAssertions wasCreatedByRefactoredMethod() {
        isValidChapter();
        isActive(); // Our refactored method sets active to true by default
        followsNamingConvention();
        
        return this;
    }
}