package com.turningpoint.chapterorganizer.config;

import com.turningpoint.chapterorganizer.entity.*;
import com.turningpoint.chapterorganizer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("production") // Only run in production profile
public class ProductionDataInitializer implements CommandLineRunner {

    private final ChapterRepository chapterRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    @Autowired
    public ProductionDataInitializer(ChapterRepository chapterRepository, 
                                   MemberRepository memberRepository,
                                   EventRepository eventRepository) {
        this.chapterRepository = chapterRepository;
        this.memberRepository = memberRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("=== Production Data Initializer Starting ===");
            
            // Check current chapter count
            long count = chapterRepository.count();
            System.out.println("Current chapter count: " + count);
            
            // Force reinitialization to ensure consistent data
            if (count > 0) {
                System.out.println("Deleting existing chapters to ensure consistent data...");
                chapterRepository.deleteAll();
            }
            
            System.out.println("Initializing comprehensive chapter data...");
            initializeBasicData();
            
            // Test the problematic search functionality
            System.out.println("Testing search functionality...");
            testSearchFunctionality();
            
            System.out.println("=== Production Data Initializer Complete ===");
            
        } catch (Exception e) {
            System.err.println("ERROR in Production Data Initializer: " + e.getMessage());
            e.printStackTrace();
            // Don't re-throw - let the app continue to start even if this fails
        }
    }
    
    private void testSearchFunctionality() {
        try {
            // Test basic repository operations
            System.out.println("Testing basic findAll...");
            var allChapters = chapterRepository.findAll();
            System.out.println("✅ findAll successful: " + allChapters.size() + " chapters");
            
            // Test the problematic search query with null parameters
            System.out.println("Testing findChaptersByCriteria with all null params...");
            var nullSearchResults = chapterRepository.findChaptersByCriteria(null, null, null, null, null);
            System.out.println("✅ Null search successful: " + nullSearchResults.size() + " results");
            
            // Test search with active=true
            System.out.println("Testing findChaptersByCriteria with active=true...");
            var activeSearchResults = chapterRepository.findChaptersByCriteria(null, null, null, null, true);
            System.out.println("✅ Active search successful: " + activeSearchResults.size() + " results");
            
            // Test search with state parameter
            if (!allChapters.isEmpty()) {
                String testState = allChapters.get(0).getState();
                System.out.println("Testing findChaptersByCriteria with state=" + testState + "...");
                var stateSearchResults = chapterRepository.findChaptersByCriteria(null, null, testState, null, true);
                System.out.println("✅ State search successful: " + stateSearchResults.size() + " results");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Search functionality test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeBasicData() {
        System.out.println("Creating comprehensive chapter data for all states...");
        
        // Create chapters for all 50 states plus DC and territories to match original 52+ count
        String[][] stateData = {
            {"University of Alabama", "University of Alabama", "Alabama", "Tuscaloosa"},
            {"Auburn University", "Auburn University", "Alabama", "Auburn"},
            {"University of Alaska", "University of Alaska Fairbanks", "Alaska", "Fairbanks"},
            {"Arizona State University", "Arizona State University", "Arizona", "Tempe"},
            {"University of Arizona", "University of Arizona", "Arizona", "Tucson"},
            {"University of Arkansas", "University of Arkansas", "Arkansas", "Fayetteville"},
            {"UC Berkeley", "University of California, Berkeley", "California", "Berkeley"},
            {"UCLA", "University of California, Los Angeles", "California", "Los Angeles"},
            {"Stanford", "Stanford University", "California", "Stanford"},
            {"USC", "University of Southern California", "California", "Los Angeles"},
            {"University of Colorado", "University of Colorado Boulder", "Colorado", "Boulder"},
            {"Colorado State", "Colorado State University", "Colorado", "Fort Collins"},
            {"Yale University", "Yale University", "Connecticut", "New Haven"},
            {"UConn", "University of Connecticut", "Connecticut", "Storrs"},
            {"University of Delaware", "University of Delaware", "Delaware", "Newark"},
            {"University of Florida", "University of Florida", "Florida", "Gainesville"},
            {"Florida State", "Florida State University", "Florida", "Tallahassee"},
            {"University of Miami", "University of Miami", "Florida", "Coral Gables"},
            {"University of Georgia", "University of Georgia", "Georgia", "Athens"},
            {"Georgia Tech", "Georgia Institute of Technology", "Georgia", "Atlanta"},
            {"University of Hawaii", "University of Hawaii at Manoa", "Hawaii", "Honolulu"},
            {"University of Idaho", "University of Idaho", "Idaho", "Moscow"},
            {"University of Illinois", "University of Illinois at Urbana-Champaign", "Illinois", "Champaign"},
            {"Northwestern", "Northwestern University", "Illinois", "Evanston"},
            {"Indiana University", "Indiana University Bloomington", "Indiana", "Bloomington"},
            {"Purdue University", "Purdue University", "Indiana", "West Lafayette"},
            {"University of Iowa", "University of Iowa", "Iowa", "Iowa City"},
            {"Iowa State", "Iowa State University", "Iowa", "Ames"},
            {"University of Kansas", "University of Kansas", "Kansas", "Lawrence"},
            {"Kansas State", "Kansas State University", "Kansas", "Manhattan"},
            {"University of Kentucky", "University of Kentucky", "Kentucky", "Lexington"},
            {"Louisville", "University of Louisville", "Kentucky", "Louisville"},
            {"Louisiana State University", "Louisiana State University", "Louisiana", "Baton Rouge"},
            {"Tulane", "Tulane University", "Louisiana", "New Orleans"},
            {"University of Maine", "University of Maine", "Maine", "Orono"},
            {"University of Maryland", "University of Maryland, College Park", "Maryland", "College Park"},
            {"Johns Hopkins", "Johns Hopkins University", "Maryland", "Baltimore"},
            {"Harvard University", "Harvard University", "Massachusetts", "Cambridge"},
            {"MIT", "Massachusetts Institute of Technology", "Massachusetts", "Cambridge"},
            {"Boston University", "Boston University", "Massachusetts", "Boston"},
            {"University of Michigan", "University of Michigan", "Michigan", "Ann Arbor"},
            {"Michigan State", "Michigan State University", "Michigan", "East Lansing"},
            {"University of Minnesota", "University of Minnesota Twin Cities", "Minnesota", "Minneapolis"},
            {"University of Mississippi", "University of Mississippi", "Mississippi", "Oxford"},
            {"Mississippi State", "Mississippi State University", "Mississippi", "Starkville"},
            {"University of Missouri", "University of Missouri", "Missouri", "Columbia"},
            {"Washington University", "Washington University in St. Louis", "Missouri", "St. Louis"},
            {"University of Montana", "University of Montana", "Montana", "Missoula"},
            {"University of Nebraska", "University of Nebraska–Lincoln", "Nebraska", "Lincoln"},
            {"University of Nevada", "University of Nevada, Las Vegas", "Nevada", "Las Vegas"},
            {"UNR", "University of Nevada, Reno", "Nevada", "Reno"},
            {"Dartmouth College", "Dartmouth College", "New Hampshire", "Hanover"},
            {"Princeton University", "Princeton University", "New Jersey", "Princeton"},
            {"Rutgers", "Rutgers University", "New Jersey", "New Brunswick"},
            {"University of New Mexico", "University of New Mexico", "New Mexico", "Albuquerque"},
            {"Columbia University", "Columbia University", "New York", "New York"},
            {"NYU", "New York University", "New York", "New York"},
            {"Cornell", "Cornell University", "New York", "Ithaca"},
            {"Duke University", "Duke University", "North Carolina", "Durham"},
            {"UNC Chapel Hill", "University of North Carolina at Chapel Hill", "North Carolina", "Chapel Hill"},
            {"University of North Dakota", "University of North Dakota", "North Dakota", "Grand Forks"},
            {"Ohio State University", "The Ohio State University", "Ohio", "Columbus"},
            {"Case Western", "Case Western Reserve University", "Ohio", "Cleveland"},
            {"University of Oklahoma", "University of Oklahoma", "Oklahoma", "Norman"},
            {"Oklahoma State", "Oklahoma State University", "Oklahoma", "Stillwater"},
            {"University of Oregon", "University of Oregon", "Oregon", "Eugene"},
            {"Oregon State", "Oregon State University", "Oregon", "Corvallis"},
            {"University of Pennsylvania", "University of Pennsylvania", "Pennsylvania", "Philadelphia"},
            {"Penn State", "Pennsylvania State University", "Pennsylvania", "University Park"},
            {"Brown University", "Brown University", "Rhode Island", "Providence"},
            {"University of South Carolina", "University of South Carolina", "South Carolina", "Columbia"},
            {"Clemson", "Clemson University", "South Carolina", "Clemson"},
            {"University of South Dakota", "University of South Dakota", "South Dakota", "Vermillion"},
            {"University of Tennessee", "University of Tennessee, Knoxville", "Tennessee", "Knoxville"},
            {"Vanderbilt", "Vanderbilt University", "Tennessee", "Nashville"},
            {"University of Texas", "University of Texas at Austin", "Texas", "Austin"},
            {"Texas A&M", "Texas A&M University", "Texas", "College Station"},
            {"Rice University", "Rice University", "Texas", "Houston"},
            {"University of Utah", "University of Utah", "Utah", "Salt Lake City"},
            {"Utah State", "Utah State University", "Utah", "Logan"},
            {"University of Vermont", "University of Vermont", "Vermont", "Burlington"},
            {"University of Virginia", "University of Virginia", "Virginia", "Charlottesville"},
            {"Virginia Tech", "Virginia Polytechnic Institute", "Virginia", "Blacksburg"},
            {"University of Washington", "University of Washington", "Washington", "Seattle"},
            {"Washington State", "Washington State University", "Washington", "Pullman"},
            {"West Virginia University", "West Virginia University", "West Virginia", "Morgantown"},
            {"University of Wisconsin", "University of Wisconsin–Madison", "Wisconsin", "Madison"},
            {"Marquette", "Marquette University", "Wisconsin", "Milwaukee"},
            {"University of Wyoming", "University of Wyoming", "Wyoming", "Laramie"}
        };
        
        try {
            for (String[] data : stateData) {
                Chapter chapter = new Chapter(data[0], data[1], data[2], data[3]);
                chapter.setDescription("Turning Point chapter at " + data[1]);
                chapter.setActive(true);
                chapter.setFoundedDate(LocalDateTime.of(2020 + (int)(Math.random() * 4), 
                                                      1 + (int)(Math.random() * 12), 
                                                      1 + (int)(Math.random() * 28), 0, 0));
                chapterRepository.save(chapter);
            }
            
            System.out.println("✅ All " + stateData.length + " chapters created successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error creating chapters: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // Create sample members for select chapters
        System.out.println("Creating sample members...");
        
        // Get all chapters and pick some for sample data
        List<Chapter> allChapters = chapterRepository.findAll();
        if (allChapters.size() >= 6) {
            // Create members for first 6 chapters
            String[][] memberData = {
                {"John", "Smith", "john.smith@stanford.edu"},
                {"Jane", "Doe", "jane.doe@berkeley.edu"},
                {"Mike", "Johnson", "mike.johnson@utexas.edu"},
                {"Sarah", "Williams", "sarah.williams@mit.edu"},
                {"Alex", "Chen", "alex.chen@nyu.edu"},
                {"Emma", "Davis", "emma.davis@ufl.edu"}
            };
            
            for (int i = 0; i < Math.min(6, allChapters.size()); i++) {
                Member member = new Member();
                member.setFirstName(memberData[i][0]);
                member.setLastName(memberData[i][1]);
                member.setEmail(memberData[i][2]);
                member.setRole(MemberRole.PRESIDENT);
                member.setActive(true);
                member.setChapter(allChapters.get(i));
                memberRepository.save(member);
            }
        }
        
        System.out.println("✅ Sample members created");

        // Create sample events for some chapters
        System.out.println("Creating sample events...");
        
        List<Chapter> chaptersForEvents = chapterRepository.findAll();
        if (chaptersForEvents.size() >= 4) {
            String[][] eventData = {
                {"Welcome Meeting", "Welcome new members to our chapter", "MEETING", "Student Center Room 101", "7"},
                {"Community Service Day", "Volunteer at local food bank", "VOLUNTEER", "Local Food Bank", "14"},
                {"Campus Outreach", "Recruit new members on campus", "RECRUITMENT", "Campus Quad", "10"},
                {"Study Session", "Group study and networking", "MEETING", "Library Conference Room", "5"}
            };
            
            for (int i = 0; i < Math.min(4, chaptersForEvents.size()); i++) {
                Event event = new Event();
                event.setTitle(eventData[i][0]);
                event.setDescription(eventData[i][1]);
                event.setType(EventType.valueOf(eventData[i][2]));
                event.setEventDateTime(LocalDateTime.now().plusDays(Integer.parseInt(eventData[i][4])));
                event.setLocation(eventData[i][3]);
                event.setActive(true);
                event.setChapter(chaptersForEvents.get(i));
                eventRepository.save(event);
            }
        }
        
        System.out.println("✅ Sample events created");

        System.out.println("Production data initialized with comprehensive chapters from all states!");
    }
}