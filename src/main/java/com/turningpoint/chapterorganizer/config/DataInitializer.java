package com.turningpoint.chapterorganizer.config;

import com.turningpoint.chapterorganizer.entity.*;
import com.turningpoint.chapterorganizer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@Profile("!production") // Only run in non-production profiles
public class DataInitializer implements CommandLineRunner {

    private final ChapterRepository chapterRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    @Autowired
    public DataInitializer(ChapterRepository chapterRepository, 
                          MemberRepository memberRepository,
                          EventRepository eventRepository) {
        this.chapterRepository = chapterRepository;
        this.memberRepository = memberRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (chapterRepository.count() > 0) {
            System.out.println("Data already exists, skipping initialization...");
            return;
        }

        System.out.println("Initializing sample data...");

        // Create sample chapters
        Chapter chapter1 = createChapter("UC Berkeley", "University of California, Berkeley", "California", "Berkeley", "The flagship chapter at UC Berkeley");
        Chapter chapter2 = createChapter("UCLA", "University of California, Los Angeles", "California", "Los Angeles", "Active chapter at UCLA");
        Chapter chapter3 = createChapter("Stanford", "Stanford University", "California", "Stanford", "Chapter at Stanford University");

        // Save chapters
        chapter1 = chapterRepository.save(chapter1);
        chapter2 = chapterRepository.save(chapter2);
        chapter3 = chapterRepository.save(chapter3);

        // Create sample members for chapter1
        Member president1 = createMember("John", "Doe", "john.doe@berkeley.edu", MemberRole.PRESIDENT, chapter1);
        Member vicePresident1 = createMember("Jane", "Smith", "jane.smith@berkeley.edu", MemberRole.VICE_PRESIDENT, chapter1);
        Member secretary1 = createMember("Mike", "Johnson", "mike.johnson@berkeley.edu", MemberRole.SECRETARY, chapter1);
        Member treasurer1 = createMember("Sarah", "Williams", "sarah.williams@berkeley.edu", MemberRole.TREASURER, chapter1);
        Member member1 = createMember("Alex", "Brown", "alex.brown@berkeley.edu", MemberRole.MEMBER, chapter1);
        Member member2 = createMember("Emily", "Davis", "emily.davis@berkeley.edu", MemberRole.MEMBER, chapter1);

        // Save members
        memberRepository.saveAll(Arrays.asList(president1, vicePresident1, secretary1, treasurer1, member1, member2));

        // Create sample members for chapter2
        Member president2 = createMember("David", "Wilson", "david.wilson@ucla.edu", MemberRole.PRESIDENT, chapter2);
        Member vicePresident2 = createMember("Lisa", "Anderson", "lisa.anderson@ucla.edu", MemberRole.VICE_PRESIDENT, chapter2);
        Member member3 = createMember("Chris", "Martinez", "chris.martinez@ucla.edu", MemberRole.MEMBER, chapter2);
        Member member4 = createMember("Rachel", "Taylor", "rachel.taylor@ucla.edu", MemberRole.MEMBER, chapter2);

        memberRepository.saveAll(Arrays.asList(president2, vicePresident2, member3, member4));

        // Create sample members for chapter3
        Member president3 = createMember("Kevin", "Lee", "kevin.lee@stanford.edu", MemberRole.PRESIDENT, chapter3);
        Member member5 = createMember("Amy", "Chen", "amy.chen@stanford.edu", MemberRole.MEMBER, chapter3);

        memberRepository.saveAll(Arrays.asList(president3, member5));

        // Create sample events
        Event event1 = createEvent("Weekly Chapter Meeting", "Regular weekly meeting to discuss chapter activities", 
            LocalDateTime.now().plusDays(2), "Student Union Room 201", EventType.MEETING, chapter1);
        
        Event event2 = createEvent("Community Service Day", "Volunteer at local food bank", 
            LocalDateTime.now().plusDays(7), "Berkeley Food Bank", EventType.VOLUNTEER, chapter1);
        
        Event event3 = createEvent("Guest Speaker Event", "Professional development speaker", 
            LocalDateTime.now().plusDays(14), "Lecture Hall A", EventType.EDUCATIONAL, chapter1);

        Event event4 = createEvent("Social Mixer", "Chapter social event with refreshments", 
            LocalDateTime.now().plusDays(10), "Campus Recreation Center", EventType.SOCIAL, chapter2);

        Event event5 = createEvent("Study Group", "Group study session for finals", 
            LocalDateTime.now().plusDays(5), "Library Study Room 3", EventType.EDUCATIONAL, chapter2);

        Event event6 = createEvent("Leadership Workshop", "Workshop on effective leadership skills", 
            LocalDateTime.now().plusDays(12), "Student Center", EventType.NETWORKING, chapter3);

        // Save events
        eventRepository.saveAll(Arrays.asList(event1, event2, event3, event4, event5, event6));

        System.out.println("Sample data initialization completed successfully!");
        System.out.println("Created " + chapterRepository.count() + " chapters, " + 
                          memberRepository.count() + " members, and " + 
                          eventRepository.count() + " events.");
    }

    private Chapter createChapter(String name, String universityName, String state, String city, String description) {
        Chapter chapter = new Chapter();
        chapter.setName(name);
        chapter.setUniversityName(universityName);
        chapter.setState(state);
        chapter.setCity(city);
        chapter.setDescription(description);
        chapter.setActive(true);
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
}