package com.turningpoint.chapterorganizer.data;

import java.util.*;

/**
 * Extracted university data to fix God Object code smell.
 * Single Responsibility: Store and organize university data by state.
 * Fixes: Large Class and duplicated data initialization.
 */
public class UniversityData {
    
    private static final Map<String, List<String>> UNIVERSITIES_BY_STATE = new HashMap<>();

    static {
        initializeUniversityData();
    }
    
    public static Map<String, List<String>> getAllUniversitiesByState() {
        return Collections.unmodifiableMap(UNIVERSITIES_BY_STATE);
    }
    
    public static List<String> getUniversitiesByState(String state) {
        return UNIVERSITIES_BY_STATE.getOrDefault(state, Collections.emptyList());
    }
    
    public static Set<String> getAllStates() {
        return Collections.unmodifiableSet(UNIVERSITIES_BY_STATE.keySet());
    }
    
    private static void addUniversities(String state, String... universities) {
        UNIVERSITIES_BY_STATE.put(state, Collections.unmodifiableList(Arrays.asList(universities)));
    }
    
    private static void initializeUniversityData() {
        // California
        addUniversities("California",
            "University of California, Berkeley",
            "University of California, Los Angeles (UCLA)",
            "University of California, San Diego (UCSD)",
            "Stanford University",
            "California Institute of Technology (Caltech)",
            "University of Southern California (USC)"
        );

        // New York  
        addUniversities("New York",
            "Columbia University",
            "New York University (NYU)",
            "Cornell University",
            "University at Buffalo",
            "Syracuse University"
        );

        // Texas
        addUniversities("Texas",
            "University of Texas at Austin",
            "Texas A&M University",
            "Rice University",
            "University of Houston",
            "Texas Tech University"
        );

        // Massachusetts
        addUniversities("Massachusetts",
            "Harvard University",
            "Massachusetts Institute of Technology (MIT)",
            "Boston University",
            "Northeastern University",
            "Tufts University"
        );

        // Florida
        addUniversities("Florida",
            "University of Florida",
            "Florida State University",
            "University of Miami",
            "Florida Institute of Technology",
            "University of Central Florida"
        );

        // Illinois
        addUniversities("Illinois",
            "University of Chicago",
            "Northwestern University",
            "University of Illinois at Urbana-Champaign",
            "Illinois Institute of Technology",
            "DePaul University"
        );
    }
}