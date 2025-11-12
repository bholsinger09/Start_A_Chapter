package com.turningpoint.chapterorganizer.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe service for managing university data by state.
 * Implements Robert Martin's Clean Code concurrency principles:
 * - Uses ConcurrentHashMap for thread-safe shared state
 * - Returns immutable collections to prevent concurrent modification
 * - Encapsulates shared data initialization in thread-safe manner
 */
@Service
public class UniversityService {

    private static final Map<String, List<String>> UNIVERSITIES_BY_STATE = new ConcurrentHashMap<>();

    static {
        initializeUniversityData();
    }
    
    /**
     * Helper method to safely add immutable lists to the concurrent map
     */
    private static void addUniversities(String state, String... universities) {
        UNIVERSITIES_BY_STATE.put(state, Collections.unmodifiableList(Arrays.asList(universities)));
    }
    
    private static void initializeUniversityData() {
        // California
        addUniversities("California",
            "University of California, Berkeley",
            "University of California, Los Angeles (UCLA)",
            "University of California, San Diego (UCSD)",
            "University of California, Davis",
            "University of California, Irvine",
            "University of California, Santa Barbara",
            "University of California, Santa Cruz",
            "University of California, Riverside",
            "University of California, Merced",
            "Stanford University",
            "California Institute of Technology (Caltech)",
            "University of Southern California (USC)",
            "California State University, Long Beach",
            "California State University, Fullerton",
            "California State University, Northridge",
            "San Diego State University",
            "San Francisco State University",
            "California Polytechnic State University, San Luis Obispo",
            "California State Polytechnic University, Pomona",
            "Pepperdine University",
            "Santa Clara University",
            "Loyola Marymount University"
        );

        // New York
        addUniversities("New York",
            "Columbia University",
            "New York University (NYU)",
            "Cornell University",
            "University of Rochester",
            "Syracuse University",
            "Fordham University",
            "Stony Brook University (SUNY)",
            "University at Buffalo (SUNY)",
            "Albany State University (SUNY)",
            "Binghamton University (SUNY)",
            "Rensselaer Polytechnic Institute",
            "Rochester Institute of Technology",
            "St. John's University",
            "Hofstra University",
            "Ithaca College",
            "Colgate University",
            "Vassar College",
            "Barnard College",
            "The Juilliard School"
        );

        // Texas
        addUniversities("Texas",
            "University of Texas at Austin",
            "Texas A&M University",
            "Rice University",
            "Texas Tech University",
            "University of Houston",
            "Texas Christian University",
            "Baylor University",
            "University of Texas at Dallas",
            "University of Texas at San Antonio",
            "Texas State University",
            "University of North Texas",
            "Texas Woman's University",
            "Prairie View A&M University",
            "Sam Houston State University",
            "Lamar University",
            "Stephen F. Austin State University",
            "Texas Southern University",
            "Trinity University"
        );

        // Florida
        addUniversities("Florida",
            "University of Florida",
            "Florida State University",
            "University of Miami",
            "Florida Institute of Technology",
            "University of Central Florida",
            "Florida International University",
            "Florida Atlantic University",
            "Florida Agricultural and Mechanical University",
            "Nova Southeastern University",
            "Barry University",
            "Rollins College",
            "Stetson University",
            "Florida Southern College",
            "Florida Polytechnic University",
            "Embry-Riddle Aeronautical University",
            "Lynn University",
            "Saint Leo University",
            "Florida Memorial University",
            "Bethune-Cookman University",
            "Edward Waters College"
        );

        // Illinois
        addUniversities("Illinois",
            "University of Chicago",
            "Northwestern University",
            "University of Illinois at Urbana-Champaign",
            "University of Illinois at Chicago",
            "Illinois Institute of Technology",
            "DePaul University",
            "Loyola University Chicago",
            "Northern Illinois University",
            "Southern Illinois University",
            "Illinois State University",
            "Western Illinois University",
            "Eastern Illinois University",
            "Chicago State University",
            "Northeastern Illinois University",
            "Governors State University",
            "Roosevelt University",
            "Columbia College Chicago",
            "School of the Art Institute of Chicago",
            "Wheaton College",
            "Knox College"
        );

        // Add remaining major states
        addRemainingMajorStates();
        
        // Add other states with standard university patterns
        addRemainingStates();
    }

    private static void addRemainingMajorStates() {
        // Massachusetts
        addUniversities("Massachusetts",
            "Harvard University",
            "Massachusetts Institute of Technology (MIT)",
            "Boston University",
            "Northeastern University",
            "Tufts University",
            "Boston College",
            "University of Massachusetts Amherst",
            "Worcester Polytechnic Institute",
            "Brandeis University",
            "Wellesley College",
            "Smith College",
            "Mount Holyoke College",
            "Amherst College",
            "Williams College",
            "Emerson College",
            "Suffolk University",
            "Bentley University",
            "Babson College",
            "Berklee College of Music",
            "Simmons College"
        );

        // Pennsylvania
        addUniversities("Pennsylvania",
            "University of Pennsylvania",
            "Carnegie Mellon University",
            "Pennsylvania State University",
            "University of Pittsburgh",
            "Drexel University",
            "Temple University",
            "Villanova University",
            "Lehigh University",
            "Penn State University",
            "Duquesne University",
            "La Salle University",
            "Saint Joseph's University",
            "Bucknell University",
            "Lafayette College",
            "Dickinson College",
            "Franklin & Marshall College",
            "Gettysburg College",
            "Muhlenberg College",
            "Susquehanna University",
            "Ursinus College"
        );

        // Michigan
        addUniversities("Michigan",
            "University of Michigan",
            "Michigan State University",
            "Wayne State University",
            "Michigan Technological University",
            "Western Michigan University",
            "Central Michigan University",
            "Eastern Michigan University",
            "Northern Michigan University",
            "Grand Valley State University",
            "Oakland University",
            "Ferris State University",
            "Saginaw Valley State University",
            "Lake Superior State University",
            "Andrews University",
            "Calvin College",
            "Hope College",
            "Kalamazoo College",
            "Albion College",
            "Alma College",
            "Hillsdale College"
        );

        // Ohio
        addUniversities("Ohio",
            "Ohio State University",
            "Case Western Reserve University",
            "University of Cincinnati",
            "Ohio University",
            "Miami University",
            "Kent State University",
            "Bowling Green State University",
            "Wright State University",
            "Youngstown State University",
            "University of Akron",
            "Cleveland State University",
            "University of Dayton",
            "Xavier University",
            "Denison University",
            "Kenyon College",
            "Oberlin College",
            "College of Wooster",
            "Wittenberg University"
        );

        // Georgia  
        addUniversities("Georgia",
            "University of Georgia",
            "Georgia Institute of Technology",
            "Emory University",
            "Georgia State University",
            "Georgia Southern University",
            "Kennesaw State University",
            "Georgia College & State University",
            "Columbus State University",
            "Albany State University",
            "Augusta University",
            "Clayton State University",
            "Fort Valley State University",
            "Georgia Gwinnett College",
            "Middle Georgia State University",
            "Savannah State University",
            "University of West Georgia",
            "Valdosta State University",
            "Mercer University",
            "Spelman College",
            "Morehouse College"
        );

        // North Carolina
        addUniversities("North Carolina",
            "Duke University",
            "University of North Carolina at Chapel Hill",
            "North Carolina State University",
            "Wake Forest University",
            "Davidson College",
            "Elon University",
            "Appalachian State University",
            "East Carolina University",
            "University of North Carolina at Charlotte",
            "University of North Carolina at Greensboro",
            "University of North Carolina Wilmington",
            "North Carolina A&T State University",
            "North Carolina Central University",
            "Western Carolina University",
            "High Point University",
            "Guilford College",
            "Salem College",
            "Lenoir-Rhyne University",
            "Catawba College",
            "Pfeiffer University"
        );

        // Virginia
        addUniversities("Virginia",
            "University of Virginia",
            "Virginia Tech",
            "Virginia Commonwealth University",
            "James Madison University",
            "George Mason University",
            "Old Dominion University",
            "Virginia Military Institute",
            "Washington and Lee University",
            "University of Richmond",
            "Hampton University",
            "Norfolk State University",
            "Radford University",
            "Longwood University"
        );

        // Washington
        addUniversities("Washington",
            "University of Washington",
            "Washington State University",
            "Seattle University",
            "Gonzaga University",
            "Western Washington University",
            "Central Washington University",
            "Eastern Washington University",
            "Pacific Lutheran University",
            "Whitman College",
            "Evergreen State College"
        );

        // Idaho
        addUniversities("Idaho",
            "University of Idaho",
            "Boise State University",
            "Idaho State University",
            "Lewis-Clark State College",
            "College of Idaho",
            "Northwest Nazarene University",
            "Brigham Young University-Idaho",
            "College of Southern Idaho"
        );
    }

    private static void addRemainingStates() {
        // Add other states with standard university naming patterns
        String[] otherStates = {
            "Alabama", "Alaska", "Arizona", "Arkansas", "Colorado", "Connecticut",
            "Delaware", "Hawaii", "Indiana", "Iowa", "Kansas", "Kentucky",
            "Louisiana", "Maine", "Maryland", "Minnesota", "Mississippi", "Missouri",
            "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
            "North Dakota", "Oklahoma", "Oregon", "Rhode Island", "South Carolina",
            "South Dakota", "Tennessee", "Utah", "Vermont", "West Virginia", "Wisconsin", "Wyoming"
        };

        for (String state : otherStates) {
            addUniversities(state,
                "University of " + state,
                state + " State University",
                state + " Tech University"
            );
        }
    }

    /**
     * Get all universities organized by state.
     * Returns an immutable copy to prevent concurrent modification.
     * 
     * @return Immutable map of state to list of universities
     */
    public Map<String, List<String>> getAllUniversitiesByState() {
        // Return immutable copy to prevent concurrent modification
        Map<String, List<String>> immutableMap = new HashMap<>();
        UNIVERSITIES_BY_STATE.forEach((key, value) -> 
            immutableMap.put(key, Collections.unmodifiableList(new ArrayList<>(value))));
        return Collections.unmodifiableMap(immutableMap);
    }

    /**
     * Get universities for a specific state.
     * Returns an immutable list to prevent concurrent modification.
     * 
     * @param state The state name
     * @return Immutable list of universities for the state
     */
    public List<String> getUniversitiesByState(String state) {
        List<String> universities = UNIVERSITIES_BY_STATE.get(state);
        if (universities != null) {
            return Collections.unmodifiableList(new ArrayList<>(universities));
        }
        return Collections.unmodifiableList(Arrays.asList("University of " + state));
    }

    /**
     * Get all available states.
     * Returns an immutable list to prevent concurrent modification.
     * 
     * @return Immutable list of state names
     */
    public List<String> getAllStates() {
        return Collections.unmodifiableList(new ArrayList<>(UNIVERSITIES_BY_STATE.keySet()));
    }
}