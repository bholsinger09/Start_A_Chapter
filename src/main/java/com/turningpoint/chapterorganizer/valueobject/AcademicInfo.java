package com.turningpoint.chapterorganizer.valueobject;

import jakarta.persistence.Embeddable;
import java.time.Year;
import java.util.Objects;

/**
 * Academic Information Value Object
 * Immutable object representing academic details following Clean Code principles.
 * Contains validation and business logic related to academic data.
 */
@Embeddable
public final class AcademicInfo {
    
    private static final int MIN_GRADUATION_YEAR = Year.now().getValue();
    private static final int MAX_GRADUATION_YEAR = Year.now().getValue() + 10;
    
    // Null Object pattern - represents empty/unknown academic info
    public static final AcademicInfo EMPTY = new AcademicInfo("", "");
    
    private final String major;
    private final String graduationYear;

    // Constructor for JPA
    protected AcademicInfo() {
        this.major = null;
        this.graduationYear = null;
    }

    public AcademicInfo(String major, String graduationYear) {
        this.major = validateMajor(major);
        this.graduationYear = validateGraduationYear(graduationYear);
    }

    private String validateMajor(String major) {
        if (major == null || major.trim().isEmpty()) {
            return ""; // Return empty string instead of null
        }
        if (major.trim().length() > 100) {
            throw new IllegalArgumentException("Major cannot exceed 100 characters");
        }
        return major.trim();
    }

    private String validateGraduationYear(String graduationYear) {
        if (graduationYear == null || graduationYear.trim().isEmpty()) {
            return ""; // Return empty string instead of null
        }
        
        try {
            int year = Integer.parseInt(graduationYear.trim());
            if (year < MIN_GRADUATION_YEAR || year > MAX_GRADUATION_YEAR) {
                throw new IllegalArgumentException(
                    "Graduation year must be between " + MIN_GRADUATION_YEAR + 
                    " and " + MAX_GRADUATION_YEAR
                );
            }
            return String.valueOf(year);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid graduation year format: " + graduationYear);
        }
    }

    public String getMajor() {
        return major;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public boolean hasMajor() {
        return major != null && !major.trim().isEmpty();
    }

    public boolean hasGraduationYear() {
        return graduationYear != null && !graduationYear.trim().isEmpty();
    }
    
    public boolean isEmpty() {
        return (major == null || major.trim().isEmpty()) && 
               (graduationYear == null || graduationYear.trim().isEmpty());
    }

    public boolean isGraduating(int targetYear) {
        if (!hasGraduationYear()) {
            return false;
        }
        try {
            return Integer.parseInt(graduationYear) == targetYear;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getYearsUntilGraduation() {
        if (!hasGraduationYear()) {
            return -1;
        }
        try {
            return Integer.parseInt(graduationYear) - Year.now().getValue();
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public boolean isCurrentStudent() {
        return hasGraduationYear() && getYearsUntilGraduation() >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicInfo that = (AcademicInfo) o;
        return Objects.equals(major, that.major) && 
               Objects.equals(graduationYear, that.graduationYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, graduationYear);
    }

    @Override
    public String toString() {
        return "AcademicInfo{" +
                "major='" + major + '\'' +
                ", graduationYear='" + graduationYear + '\'' +
                '}';
    }
}