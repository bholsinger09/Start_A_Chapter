package com.turningpoint.chapterorganizer.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UniversityService {

    private static final Map<String, List<String>> UNIVERSITIES_BY_STATE = new HashMap<>();

    static {
        // California
        UNIVERSITIES_BY_STATE.put("California", Arrays.asList(
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
        ));

        // New York
        UNIVERSITIES_BY_STATE.put("New York", Arrays.asList(
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
            "The New School",
            "Yeshiva University",
            "St. John's University",
            "Hofstra University",
            "Ithaca College",
            "Colgate University",
            "Vassar College",
            "Barnard College",
            "The Juilliard School"
        ));

        // Texas
        UNIVERSITIES_BY_STATE.put("Texas", Arrays.asList(
            "University of Texas at Austin",
            "Texas A&M University",
            "Rice University",
            "University of Houston",
            "Texas Tech University",
            "Baylor University",
            "Texas Christian University (TCU)",
            "Southern Methodist University (SMU)",
            "University of Texas at Dallas",
            "Texas State University",
            "University of North Texas",
            "Texas A&M University at College Station",
            "University of Texas at San Antonio",
            "Texas A&M University at Commerce",
            "Prairie View A&M University",
            "Sam Houston State University",
            "Lamar University",
            "Stephen F. Austin State University",
            "Texas Southern University",
            "Trinity University"
        ));

        // Florida
        UNIVERSITIES_BY_STATE.put("Florida", Arrays.asList(
            "University of Florida",
            "Florida State University",
            "University of Miami",
            "Florida International University",
            "University of Central Florida",
            "Florida Institute of Technology",
            "Nova Southeastern University",
            "Florida Atlantic University",
            "Florida Agricultural and Mechanical University (FAMU)",
            "Florida Southern College",
            "Rollins College",
            "Stetson University",
            "Florida Polytechnic University",
            "Florida Gulf Coast University",
            "Florida A&M University",
            "Barry University",
            "Lynn University",
            "Embry-Riddle Aeronautical University",
            "Florida Memorial University",
            "Saint Leo University"
        ));

        // Illinois
        UNIVERSITIES_BY_STATE.put("Illinois", Arrays.asList(
            "University of Chicago",
            "Northwestern University",
            "University of Illinois at Urbana-Champaign",
            "University of Illinois at Chicago",
            "DePaul University",
            "Loyola University Chicago",
            "Illinois Institute of Technology",
            "Southern Illinois University",
            "Northern Illinois University",
            "Eastern Illinois University",
            "Western Illinois University",
            "Illinois State University",
            "Bradley University",
            "Wheaton College",
            "Lake Forest College",
            "Knox College",
            "Augustana College",
            "Greenville University",
            "North Central College",
            "Chicago State University"
        ));

        // Massachusetts  
        UNIVERSITIES_BY_STATE.put("Massachusetts", Arrays.asList(
            "Harvard University",
            "Massachusetts Institute of Technology (MIT)",
            "Boston University",
            "Northeastern University",
            "Tufts University",
            "Boston College",
            "University of Massachusetts Amherst",
            "Brandeis University",
            "Worcester Polytechnic Institute",
            "Emerson College",
            "Suffolk University",
            "Bentley University",
            "Babson College",
            "Wellesley College",
            "Smith College",
            "Mount Holyoke College",
            "Amherst College",
            "Williams College",
            "Clark University",
            "Simmons University"
        ));

        // Pennsylvania
        UNIVERSITIES_BY_STATE.put("Pennsylvania", Arrays.asList(
            "University of Pennsylvania",
            "Carnegie Mellon University",
            "Pennsylvania State University",
            "Temple University",
            "University of Pittsburgh",
            "Drexel University",
            "Villanova University",
            "Lehigh University",
            "Swarthmore College",
            "Haverford College",
            "Bryn Mawr College",
            "Lafayette College",
            "Bucknell University",
            "Dickinson College",
            "Franklin & Marshall College",
            "Gettysburg College",
            "Muhlenberg College",
            "Saint Joseph's University",
            "La Salle University",
            "Duquesne University"
        ));

        // Michigan
        UNIVERSITIES_BY_STATE.put("Michigan", Arrays.asList(
            "University of Michigan",
            "Michigan State University",
            "Wayne State University",
            "Western Michigan University",
            "Central Michigan University",
            "Eastern Michigan University",
            "Oakland University",
            "Grand Valley State University",
            "Michigan Technological University",
            "Northern Michigan University",
            "Ferris State University",
            "Lake Superior State University",
            "Saginaw Valley State University",
            "University of Michigan-Dearborn",
            "University of Michigan-Flint",
            "Calvin University",
            "Hope College",
            "Kalamazoo College",
            "Albion College",
            "Andrews University"
        ));

        // Ohio
        UNIVERSITIES_BY_STATE.put("Ohio", Arrays.asList(
            "The Ohio State University",
            "Case Western Reserve University",
            "University of Cincinnati",
            "Ohio University",
            "Miami University",
            "Kent State University",
            "Bowling Green State University",
            "Wright State University",
            "University of Akron",
            "Cleveland State University",
            "University of Toledo",
            "Youngstown State University",
            "Ohio University",
            "Denison University",
            "Kenyon College",
            "Oberlin College",
            "College of Wooster",
            "Wittenberg University",
            "Xavier University",
            "University of Dayton"
        ));

        // Georgia
        UNIVERSITIES_BY_STATE.put("Georgia", Arrays.asList(
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
        ));

        // North Carolina
        UNIVERSITIES_BY_STATE.put("North Carolina", Arrays.asList(
            "University of North Carolina at Chapel Hill",
            "Duke University",
            "North Carolina State University",
            "Wake Forest University",
            "Davidson College",
            "University of North Carolina at Charlotte",
            "East Carolina University",
            "Appalachian State University",
            "Western Carolina University",
            "UNC Greensboro",
            "UNC Wilmington",
            "NC Central University",
            "NC A&T State University",
            "High Point University",
            "Elon University",
            "Campbell University",
            "Guilford College",
            "Salem College",
            "Catawba College",
            "Lenoir-Rhyne University"
        ));

        // Add more states as needed...
        addRemainingStates();
    }

    private static void addRemainingStates() {
        // Virginia
        UNIVERSITIES_BY_STATE.put("Virginia", Arrays.asList(
            "University of Virginia",
            "Virginia Tech",
            "Virginia Commonwealth University",
            "James Madison University",
            "George Mason University",
            "Old Dominion University",
            "Virginia Military Institute",
            "Washington and Lee University",
            "University of Richmond",
            "Hampton University"
        ));

        // Washington
        UNIVERSITIES_BY_STATE.put("Washington", Arrays.asList(
            "University of Washington",
            "Washington State University",
            "Seattle University",
            "Gonzaga University",
            "Western Washington University",
            "Central Washington University",
            "Eastern Washington University",
            "Pacific Lutheran University",
            "Whitman College",
            "Seattle Pacific University"
        ));

        // Idaho
        UNIVERSITIES_BY_STATE.put("Idaho", Arrays.asList(
            "Boise State University",
            "University of Idaho",
            "Idaho State University",
            "Lewis-Clark State College",
            "College of Idaho",
            "Northwest Nazarene University",
            "Brigham Young University-Idaho",
            "College of Southern Idaho"
        ));

        // Add other states with fewer major universities
        String[] otherStates = {
            "Alabama", "Alaska", "Arizona", "Arkansas", "Colorado", "Connecticut",
            "Delaware", "Hawaii", "Indiana", "Iowa", "Kansas", "Kentucky",
            "Louisiana", "Maine", "Maryland", "Minnesota", "Mississippi", "Missouri",
            "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
            "North Dakota", "Oklahoma", "Oregon", "Rhode Island", "South Carolina",
            "South Dakota", "Tennessee", "Utah", "Vermont", "West Virginia", "Wisconsin", "Wyoming"
        };

        for (String state : otherStates) {
            UNIVERSITIES_BY_STATE.put(state, Arrays.asList(
                "University of " + state,
                state + " State University",
                state + " Tech University"
            ));
        }
    }

    public Map<String, List<String>> getAllUniversitiesByState() {
        return new HashMap<>(UNIVERSITIES_BY_STATE);
    }

    public List<String> getUniversitiesByState(String state) {
        return UNIVERSITIES_BY_STATE.getOrDefault(state, Arrays.asList("University of " + state));
    }

    public List<String> getAllStates() {
        return new ArrayList<>(UNIVERSITIES_BY_STATE.keySet());
    }
}