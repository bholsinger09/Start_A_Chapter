package com.turningpoint.chapterorganizer.config;

import com.turningpoint.chapterorganizer.entity.*;
import com.turningpoint.chapterorganizer.repository.*;
import com.turningpoint.chapterorganizer.service.RealTimeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

@Component
@Profile("!production") // Only run in non-production profiles
public class DataInitializer implements CommandLineRunner {

    private final ChapterRepository chapterRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final RealTimeDataService realTimeDataService;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public DataInitializer(ChapterRepository chapterRepository, 
                          MemberRepository memberRepository,
                          EventRepository eventRepository,
                          RealTimeDataService realTimeDataService,
                          RoleRepository roleRepository,
                          PermissionRepository permissionRepository,
                          UserRoleRepository userRoleRepository) {
        this.chapterRepository = chapterRepository;
        this.memberRepository = memberRepository;
        this.eventRepository = eventRepository;
        this.realTimeDataService = realTimeDataService;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (chapterRepository.count() > 0) {
            System.out.println("Data already exists, skipping initialization...");
            return;
        }

        System.out.println("Initializing comprehensive Turning Point USA chapter data for all 50 states...");

        // Create chapters for all 50 US states with realistic universities
        Chapter[] chapters = createAllStateChapters();
        
        // Create members for each chapter
        createMembersForAllChapters(chapters);
        
        // Create events for each chapter
        createEventsForAllChapters(chapters);
        
        // Create test user with roles and permissions
        createTestUserWithPermissions(chapters[0]); // Use first chapter

        System.out.println("Sample data initialization completed successfully!");
        System.out.println("Created " + chapterRepository.count() + " chapters, " + 
                          memberRepository.count() + " members, and " + 
                          eventRepository.count() + " events across all 50 states.");
    }
    
    private Chapter[] createAllStateChapters() {
        Chapter[] chapters = {
            // Alabama
            createChapter("University of Alabama", "University of Alabama", "Alabama", "Tuscaloosa", "Roll Tide! Conservative voices at the Capstone"),
            
            // Alaska  
            createChapter("University of Alaska", "University of Alaska Anchorage", "Alaska", "Anchorage", "Last frontier conservatism in the great north"),
            
            // Arizona
            createChapter("Arizona State University", "Arizona State University", "Arizona", "Tempe", "Sun Devils standing for conservative principles"),
            
            // Arkansas
            createChapter("University of Arkansas", "University of Arkansas", "Arkansas", "Fayetteville", "Razorback conservatives in the Natural State"),
            
            // California - Multiple chapters
            createChapter("UC Berkeley", "University of California, Berkeley", "California", "Berkeley", "The flagship chapter at UC Berkeley"),
            createChapter("UCLA", "University of California, Los Angeles", "California", "Los Angeles", "Active chapter at UCLA"),
            createChapter("Stanford", "Stanford University", "California", "Stanford", "Chapter at Stanford University"),
            
            // Colorado
            createChapter("University of Colorado", "University of Colorado Boulder", "Colorado", "Boulder", "Rocky Mountain conservative values"),
            
            // Connecticut
            createChapter("Yale University", "Yale University", "Connecticut", "New Haven", "Ivy League conservative leadership"),
            
            // Delaware
            createChapter("University of Delaware", "University of Delaware", "Delaware", "Newark", "First State conservative principles"),
            
            // Florida
            createChapter("University of Florida", "University of Florida", "Florida", "Gainesville", "Gator Nation conservative activism"),
            
            // Georgia
            createChapter("University of Georgia", "University of Georgia", "Georgia", "Athens", "Bulldog conservatives in the Peach State"),
            
            // Hawaii
            createChapter("University of Hawaii", "University of Hawaii at Manoa", "Hawaii", "Honolulu", "Aloha State conservative voices"),
            
            // Idaho
            createChapter("University of Idaho", "University of Idaho", "Idaho", "Moscow", "Vandal conservatives in the Gem State"),
            
            // Illinois
            createChapter("University of Illinois", "University of Illinois at Urbana-Champaign", "Illinois", "Champaign", "Fighting Illini conservative spirit"),
            
            // Indiana
            createChapter("Indiana University", "Indiana University Bloomington", "Indiana", "Bloomington", "Hoosier conservative tradition"),
            
            // Iowa
            createChapter("University of Iowa", "University of Iowa", "Iowa", "Iowa City", "Hawkeye conservative values"),
            
            // Kansas
            createChapter("University of Kansas", "University of Kansas", "Kansas", "Lawrence", "Jayhawk conservative principles"),
            
            // Kentucky
            createChapter("University of Kentucky", "University of Kentucky", "Kentucky", "Lexington", "Wildcat conservative leadership"),
            
            // Louisiana
            createChapter("Louisiana State University", "Louisiana State University", "Louisiana", "Baton Rouge", "Tiger conservative pride in the Bayou State"),
            
            // Maine
            createChapter("University of Maine", "University of Maine", "Maine", "Orono", "Black Bear conservatives in Vacationland"),
            
            // Maryland
            createChapter("University of Maryland", "University of Maryland, College Park", "Maryland", "College Park", "Terrapins for conservative values"),
            
            // Massachusetts
            createChapter("Harvard University", "Harvard University", "Massachusetts", "Cambridge", "Crimson conservative voices at America's oldest university"),
            
            // Michigan
            createChapter("University of Michigan", "University of Michigan", "Michigan", "Ann Arbor", "Wolverine conservative tradition"),
            
            // Minnesota
            createChapter("University of Minnesota", "University of Minnesota Twin Cities", "Minnesota", "Minneapolis", "Golden Gopher conservative values"),
            
            // Mississippi
            createChapter("University of Mississippi", "University of Mississippi", "Mississippi", "Oxford", "Ole Miss conservative heritage"),
            
            // Missouri
            createChapter("University of Missouri", "University of Missouri", "Missouri", "Columbia", "Tiger conservative spirit in the Show-Me State"),
            
            // Montana
            createChapter("University of Montana", "University of Montana", "Montana", "Missoula", "Grizzly conservative values in Big Sky Country"),
            
            // Nebraska
            createChapter("University of Nebraska", "University of Nebraska-Lincoln", "Nebraska", "Lincoln", "Cornhusker conservative principles"),
            
            // Nevada
            createChapter("University of Nevada", "University of Nevada, Reno", "Nevada", "Reno", "Wolf Pack conservative leadership in the Silver State"),
            
            // New Hampshire
            createChapter("University of New Hampshire", "University of New Hampshire", "New Hampshire", "Durham", "Wildcat conservatives in the Live Free or Die state"),
            
            // New Jersey
            createChapter("Rutgers University", "Rutgers, The State University of New Jersey", "New Jersey", "New Brunswick", "Scarlet Knight conservative activism"),
            
            // New Mexico
            createChapter("University of New Mexico", "University of New Mexico", "New Mexico", "Albuquerque", "Lobo conservative values in the Land of Enchantment"),
            
            // New York
            createChapter("Columbia University", "Columbia University", "New York", "New York", "Lion conservative voices in the Big Apple"),
            
            // North Carolina
            createChapter("University of North Carolina", "University of North Carolina at Chapel Hill", "North Carolina", "Chapel Hill", "Tar Heel conservative tradition"),
            
            // North Dakota
            createChapter("University of North Dakota", "University of North Dakota", "North Dakota", "Grand Forks", "Fighting Hawks conservative spirit"),
            
            // Ohio
            createChapter("Ohio State University", "The Ohio State University", "Ohio", "Columbus", "Buckeye conservative pride"),
            
            // Oklahoma
            createChapter("University of Oklahoma", "University of Oklahoma", "Oklahoma", "Norman", "Sooner conservative values"),
            
            // Oregon
            createChapter("University of Oregon", "University of Oregon", "Oregon", "Eugene", "Duck conservative voices in the Pacific Northwest"),
            
            // Pennsylvania
            createChapter("Penn State", "Pennsylvania State University", "Pennsylvania", "University Park", "Nittany Lion conservative leadership"),
            
            // Rhode Island
            createChapter("Brown University", "Brown University", "Rhode Island", "Providence", "Bear conservative principles in the Ocean State"),
            
            // South Carolina
            createChapter("University of South Carolina", "University of South Carolina", "South Carolina", "Columbia", "Gamecock conservative heritage"),
            
            // South Dakota
            createChapter("University of South Dakota", "University of South Dakota", "South Dakota", "Vermillion", "Coyote conservative values in Mount Rushmore State"),
            
            // Tennessee
            createChapter("University of Tennessee", "University of Tennessee, Knoxville", "Tennessee", "Knoxville", "Volunteer conservative spirit"),
            
            // Texas
            createChapter("University of Texas", "University of Texas at Austin", "Texas", "Austin", "Longhorn conservative pride in the Lone Star State"),
            
            // Utah
            createChapter("University of Utah", "University of Utah", "Utah", "Salt Lake City", "Ute conservative values in the Beehive State"),
            
            // Vermont
            createChapter("University of Vermont", "University of Vermont", "Vermont", "Burlington", "Catamount conservative voices in the Green Mountain State"),
            
            // Virginia
            createChapter("University of Virginia", "University of Virginia", "Virginia", "Charlottesville", "Cavalier conservative tradition in the Old Dominion"),
            
            // Washington
            createChapter("University of Washington", "University of Washington", "Washington", "Seattle", "Husky conservative leadership in the Evergreen State"),
            
            // West Virginia
            createChapter("West Virginia University", "West Virginia University", "West Virginia", "Morgantown", "Mountaineer conservative spirit"),
            
            // Wisconsin
            createChapter("University of Wisconsin", "University of Wisconsin-Madison", "Wisconsin", "Madison", "Badger conservative values in America's Dairyland"),
            
            // Wyoming
            createChapter("University of Wyoming", "University of Wyoming", "Wyoming", "Laramie", "Cowboy conservative principles in the Equality State")
        };

        // Save all chapters
        for (int i = 0; i < chapters.length; i++) {
            chapters[i] = chapterRepository.save(chapters[i]);
        }

        return chapters;
    }
    
    private void createMembersForAllChapters(Chapter[] chapters) {
        String[] firstNames = {
            "James", "John", "Robert", "Michael", "David", "William", "Richard", "Joseph", "Thomas", "Christopher",
            "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica", "Sarah", "Karen",
            "Daniel", "Matthew", "Anthony", "Mark", "Donald", "Steven", "Paul", "Andrew", "Joshua", "Kenneth",
            "Nancy", "Lisa", "Betty", "Helen", "Sandra", "Donna", "Carol", "Ruth", "Sharon", "Michelle",
            "Charles", "Brian", "Kevin", "Ronald", "Jason", "Edward", "Jeffrey", "Ryan", "Jacob", "Gary"
        };
        
        String[] lastNames = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
            "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson",
            "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
            "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell", "Carter", "Roberts"
        };

        Random random = new Random();
        
        for (Chapter chapter : chapters) {
            // Create leadership for each chapter
            String universityDomain = getUniversityEmailDomain(chapter.getUniversityName());
            
            // President
            String presFirstName = firstNames[random.nextInt(firstNames.length)];
            String presLastName = lastNames[random.nextInt(lastNames.length)];
            Member president = createMember(presFirstName, presLastName, 
                presFirstName.toLowerCase() + "." + presLastName.toLowerCase() + ".pres@" + universityDomain, 
                MemberRole.PRESIDENT, chapter);
            
            // Vice President
            String vpFirstName = firstNames[random.nextInt(firstNames.length)];
            String vpLastName = lastNames[random.nextInt(lastNames.length)];
            Member vicePresident = createMember(vpFirstName, vpLastName,
                vpFirstName.toLowerCase() + "." + vpLastName.toLowerCase() + ".vp@" + universityDomain,
                MemberRole.VICE_PRESIDENT, chapter);
            
            // Secretary (some chapters)
            Member secretary = null;
            if (random.nextBoolean()) {
                String secFirstName = firstNames[random.nextInt(firstNames.length)];
                String secLastName = lastNames[random.nextInt(lastNames.length)];
                secretary = createMember(secFirstName, secLastName,
                    secFirstName.toLowerCase() + "." + secLastName.toLowerCase() + ".sec@" + universityDomain,
                    MemberRole.SECRETARY, chapter);
            }
            
            // Treasurer (some chapters)
            Member treasurer = null;
            if (random.nextBoolean()) {
                String treasFirstName = firstNames[random.nextInt(firstNames.length)];
                String treasLastName = lastNames[random.nextInt(lastNames.length)];
                treasurer = createMember(treasFirstName, treasLastName,
                    treasFirstName.toLowerCase() + "." + treasLastName.toLowerCase() + ".treas@" + universityDomain,
                    MemberRole.TREASURER, chapter);
            }
            
            // Get real-time based member count
            int memberCount = realTimeDataService.getMemberCountForChapter(chapter.getUniversityName(), chapter.getState());
            List<Member> allMembers = new ArrayList<>();
            allMembers.add(president);
            allMembers.add(vicePresident);
            if (secretary != null) allMembers.add(secretary);
            if (treasurer != null) allMembers.add(treasurer);
            
            for (int i = 0; i < memberCount; i++) {
                String memFirstName = firstNames[random.nextInt(firstNames.length)];
                String memLastName = lastNames[random.nextInt(lastNames.length)];
                // Add unique identifier to prevent duplicate emails
                String email = memFirstName.toLowerCase() + "." + memLastName.toLowerCase() + "." + (i+1) + "@" + universityDomain;
                Member member = createMember(memFirstName, memLastName, email, MemberRole.MEMBER, chapter);
                allMembers.add(member);
            }
            
            memberRepository.saveAll(allMembers);
        }
    }
    
    private void createEventsForAllChapters(Chapter[] chapters) {
        String[] meetingTitles = {
            "Weekly Chapter Meeting", "Leadership Council", "Planning Session", "Officer Meeting",
            "Strategic Planning Meeting", "Committee Meeting", "Board Meeting"
        };
        
        String[] eventTitles = {
            "Conservative Speaker Series", "Constitution Day Celebration", "Entrepreneurship Workshop",
            "Leadership Training", "Community Service Day", "Voter Registration Drive", 
            "Political Awareness Forum", "Campus Free Speech Rally", "Conservative Film Screening",
            "Debate Night", "Social Mixer", "Networking Event", "Fundraising Dinner",
            "Campus Outreach", "Study Session", "Book Club Meeting", "Policy Discussion"
        };
        
        String[] locations = {
            "Student Union", "Library Conference Room", "Campus Center", "Auditorium", 
            "Lecture Hall", "Meeting Room A", "Student Activity Center", "Campus Quad",
            "Recreation Center", "Academic Building", "Student Center", "Community Center"
        };

        Random random = new Random();
        
        for (Chapter chapter : chapters) {
            // Create 3-6 events per chapter
            int eventCount = 3 + random.nextInt(4);
            List<Event> chapterEvents = new ArrayList<>();
            
            for (int i = 0; i < eventCount; i++) {
                String title;
                EventType type;
                
                // First event is always a meeting
                if (i == 0) {
                    title = meetingTitles[random.nextInt(meetingTitles.length)];
                    type = EventType.MEETING;
                } else {
                    title = eventTitles[random.nextInt(eventTitles.length)];
                    EventType[] types = EventType.values();
                    type = types[random.nextInt(types.length)];
                }
                
                String location = locations[random.nextInt(locations.length)] + " - " + chapter.getCity();
                String description = "Join " + chapter.getName() + " for " + title.toLowerCase() + 
                                  ". All members and interested students welcome!";
                
                // Schedule events 1-30 days from now
                LocalDateTime eventDateTime = LocalDateTime.now().plusDays(1 + random.nextInt(30));
                
                Event event = createEvent(title, description, eventDateTime, location, type, chapter);
                chapterEvents.add(event);
            }
            
            eventRepository.saveAll(chapterEvents);
        }
    }
    

    
    private String getUniversityEmailDomain(String universityName) {
        // Simplified email domain mapping
        if (universityName.contains("Berkeley")) return "berkeley.edu";
        if (universityName.contains("UCLA")) return "ucla.edu";
        if (universityName.contains("Stanford")) return "stanford.edu";
        if (universityName.contains("Harvard")) return "harvard.edu";
        if (universityName.contains("Yale")) return "yale.edu";
        if (universityName.contains("Columbia")) return "columbia.edu";
        if (universityName.contains("Brown")) return "brown.edu";
        if (universityName.contains("Alabama")) return "ua.edu";
        if (universityName.contains("Alaska")) return "uaa.alaska.edu";
        if (universityName.contains("Arizona State")) return "asu.edu";
        if (universityName.contains("Arkansas")) return "uark.edu";
        if (universityName.contains("Colorado")) return "colorado.edu";
        if (universityName.contains("Connecticut")) return "uconn.edu";
        if (universityName.contains("Delaware")) return "udel.edu";
        if (universityName.contains("Florida")) return "ufl.edu";
        if (universityName.contains("Georgia")) return "uga.edu";
        if (universityName.contains("Hawaii")) return "hawaii.edu";
        if (universityName.contains("Idaho")) return "uidaho.edu";
        if (universityName.contains("Illinois")) return "illinois.edu";
        if (universityName.contains("Indiana")) return "indiana.edu";
        if (universityName.contains("Iowa")) return "uiowa.edu";
        if (universityName.contains("Kansas")) return "ku.edu";
        if (universityName.contains("Kentucky")) return "uky.edu";
        if (universityName.contains("Louisiana State")) return "lsu.edu";
        if (universityName.contains("Maine")) return "maine.edu";
        if (universityName.contains("Maryland")) return "umd.edu";
        if (universityName.contains("Massachusetts")) return "umass.edu";
        if (universityName.contains("Michigan")) return "umich.edu";
        if (universityName.contains("Minnesota")) return "umn.edu";
        if (universityName.contains("Mississippi")) return "olemiss.edu";
        if (universityName.contains("Missouri")) return "missouri.edu";
        if (universityName.contains("Montana")) return "umt.edu";
        if (universityName.contains("Nebraska")) return "unl.edu";
        if (universityName.contains("Nevada")) return "unr.edu";
        if (universityName.contains("New Hampshire")) return "unh.edu";
        if (universityName.contains("Rutgers")) return "rutgers.edu";
        if (universityName.contains("New Mexico")) return "unm.edu";
        if (universityName.contains("North Carolina")) return "unc.edu";
        if (universityName.contains("North Dakota")) return "und.edu";
        if (universityName.contains("Ohio State")) return "osu.edu";
        if (universityName.contains("Oklahoma")) return "ou.edu";
        if (universityName.contains("Oregon")) return "uoregon.edu";
        if (universityName.contains("Penn State")) return "psu.edu";
        if (universityName.contains("South Carolina")) return "sc.edu";
        if (universityName.contains("South Dakota")) return "usd.edu";
        if (universityName.contains("Tennessee")) return "utk.edu";
        if (universityName.contains("Texas")) return "utexas.edu";
        if (universityName.contains("Utah")) return "utah.edu";
        if (universityName.contains("Vermont")) return "uvm.edu";
        if (universityName.contains("Virginia")) return "virginia.edu";
        if (universityName.contains("Washington")) return "uw.edu";
        if (universityName.contains("West Virginia")) return "wvu.edu";
        if (universityName.contains("Wisconsin")) return "wisc.edu";
        if (universityName.contains("Wyoming")) return "uwyo.edu";
        
        // Default fallback
        return "university.edu";
    }

    private Chapter createChapter(String name, String universityName, String state, String city, String description) {
        Chapter chapter = new Chapter();
        chapter.setName(name);
        chapter.setUniversityName(universityName);
        chapter.setState(state);
        chapter.setCity(city);
        chapter.setDescription(description);
        chapter.setActive(true);
        // Set a random founded date between 2018 and 2023
        LocalDateTime foundedDate = LocalDateTime.of(2018 + (int)(Math.random() * 6), 
                                                   1 + (int)(Math.random() * 12), 
                                                   1 + (int)(Math.random() * 28), 
                                                   0, 0);
        chapter.setFoundedDate(foundedDate);
        return chapter;
    }

    private Member createMember(String firstName, String lastName, String email, MemberRole role, Chapter chapter) {
        Member member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail(email);
        member.setRole(role);
        member.setChapter(chapter);
        member.setActive(true);
        return member;
    }

    private Event createEvent(String title, String description, LocalDateTime eventDateTime, 
                            String location, EventType type, Chapter chapter) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setEventDateTime(eventDateTime);
        event.setLocation(location);
        event.setType(type);
        event.setChapter(chapter);
        event.setMaxAttendees(50);
        event.setCurrentAttendees(0);
        event.setActive(true);
        return event;
    }
    
    /**
     * Create a test user with roles and permissions for authentication testing
     */
    private void createTestUserWithPermissions(Chapter chapter) {
        try {
            System.out.println("Creating test user with roles and permissions...");
            
            // Create basic permissions
            Permission readPermission = createOrGetPermission("chapter:read", "Read chapter data");
            Permission writePermission = createOrGetPermission("chapter:write", "Write chapter data");
            Permission memberReadPermission = createOrGetPermission("member:read", "Read member data");
            Permission memberWritePermission = createOrGetPermission("member:write", "Write member data");
            Permission eventReadPermission = createOrGetPermission("event:read", "Read event data");
            Permission eventWritePermission = createOrGetPermission("event:write", "Write event data");
            
            // Create test role with permissions
            Role testRole = createOrGetRole("TEST_USER", "Test user role with basic permissions", 5);
            if (testRole.getPermissions().isEmpty()) {
                testRole.getPermissions().addAll(Arrays.asList(
                    readPermission, writePermission, memberReadPermission, 
                    memberWritePermission, eventReadPermission, eventWritePermission
                ));
                roleRepository.save(testRole);
            }
            
            // Create test user
            Member testUser = new Member();
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setEmail("testuser@turningpoint.org");
            testUser.setPhoneNumber("555-0123");
            testUser.setRole(MemberRole.PRESIDENT); // Give high-level access
            testUser.setActive(true);
            testUser.setMajor("Political Science");
            testUser.setGraduationYear("2025");
            testUser.setChapter(chapter);
            testUser = memberRepository.save(testUser);
            
            // Assign role to test user
            UserRole userRole = new UserRole();
            userRole.setUser(testUser);
            userRole.setRole(testRole);
            userRole.setChapter(chapter); // Chapter-scoped role
            userRole.setIsActive(true);
            userRole.setGrantedAt(LocalDateTime.now());
            userRole.setGrantedBy(testUser); // Self-granted for initialization
            userRoleRepository.save(userRole);
            
            System.out.println("Test user created successfully:");
            System.out.println("Username: testuser");
            System.out.println("Password: password123");
            System.out.println("Email: testuser@turningpoint.org");
            System.out.println("Role: " + testRole.getName() + " with " + testRole.getPermissions().size() + " permissions");
            
        } catch (Exception e) {
            System.err.println("Error creating test user: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Permission createOrGetPermission(String name, String description) {
        return permissionRepository.findByName(name).orElseGet(() -> {
            String[] parts = name.split(":");
            String resource = parts.length > 0 ? parts[0] : "general";
            String action = parts.length > 1 ? parts[1] : "access";
            
            Permission permission = new Permission();
            permission.setName(name);
            permission.setResource(resource);
            permission.setAction(action);
            permission.setDescription(description);
            permission.setIsSystemPermission(false);
            return permissionRepository.save(permission);
        });
    }
    
    private Role createOrGetRole(String name, String description, int hierarchyLevel) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            role.setHierarchyLevel(hierarchyLevel);
            role.setIsSystemRole(false);
            role.setIsAssignable(true);
            role.setCreatedAt(LocalDateTime.now());
            return roleRepository.save(role);
        });
    }
}