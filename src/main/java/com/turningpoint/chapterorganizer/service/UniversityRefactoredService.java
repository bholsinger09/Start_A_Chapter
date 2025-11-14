package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.data.UniversityData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Refactored UniversityService - now focused and small.
 * Fixes: Large Class, God Object code smells.
 * Single Responsibility: Provide university lookup operations.
 */
@Service
public class UniversityRefactoredService {

    /**
     * Get all universities organized by state.
     * Delegates to data layer for separation of concerns.
     */
    public Map<String, List<String>> getAllUniversitiesByState() {
        return UniversityData.getAllUniversitiesByState();
    }

    /**
     * Get universities for a specific state.
     * Focused method with single responsibility.
     */
    public List<String> getUniversitiesForState(String state) {
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException("State cannot be null or empty");
        }
        return UniversityData.getUniversitiesByState(state.trim());
    }

    /**
     * Get all available states.
     * Simple, focused method.
     */
    public Set<String> getAvailableStates() {
        return UniversityData.getAllStates();
    }

    /**
     * Check if a university exists in a given state.
     * Focused validation method.
     */
    public boolean isUniversityInState(String university, String state) {
        if (university == null || state == null) {
            return false;
        }
        
        List<String> universities = getUniversitiesForState(state);
        return universities.stream()
                .anyMatch(u -> u.equalsIgnoreCase(university.trim()));
    }
}