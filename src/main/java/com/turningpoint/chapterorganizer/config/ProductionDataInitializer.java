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
        // Only initialize if no chapters exist
        if (chapterRepository.count() == 0) {
            initializeBasicData();
        }
    }

    private void initializeBasicData() {
        // Create a few sample chapters for production using constructor
        Chapter stanford = new Chapter("Stanford Turning Point", "Stanford University", "California", "Stanford");
        stanford.setDescription("Turning Point chapter at Stanford University");
        stanford.setActive(true);

        Chapter berkeley = new Chapter("UC Berkeley Turning Point", "University of California, Berkeley", "California", "Berkeley");
        berkeley.setDescription("Turning Point chapter at UC Berkeley");
        berkeley.setActive(true);

        // Save chapters
        stanford = chapterRepository.save(stanford);
        berkeley = chapterRepository.save(berkeley);

        // Create sample members
        Member member1 = new Member();
        member1.setFirstName("John");
        member1.setLastName("Smith");
        member1.setEmail("john.smith@stanford.edu");
        member1.setRole(MemberRole.PRESIDENT);
        member1.setActive(true);
        member1.setChapter(stanford);

        Member member2 = new Member();
        member2.setFirstName("Jane");
        member2.setLastName("Doe");
        member2.setEmail("jane.doe@berkeley.edu");
        member2.setRole(MemberRole.PRESIDENT);
        member2.setActive(true);
        member2.setChapter(berkeley);

        memberRepository.saveAll(Arrays.asList(member1, member2));

        // Create sample events
        Event event1 = new Event();
        event1.setTitle("Welcome Meeting");
        event1.setDescription("Welcome new members to our chapter");
        event1.setType(EventType.MEETING);
        event1.setEventDateTime(LocalDateTime.now().plusDays(7));
        event1.setLocation("Student Center Room 101");
        event1.setActive(true);
        event1.setChapter(stanford);

        Event event2 = new Event();
        event2.setTitle("Community Service Day");
        event2.setDescription("Volunteer at local food bank");
        event2.setType(EventType.VOLUNTEER);
        event2.setEventDateTime(LocalDateTime.now().plusDays(14));
        event2.setLocation("Berkeley Food Bank");
        event2.setActive(true);
        event2.setChapter(berkeley);

        eventRepository.saveAll(Arrays.asList(event1, event2));

        System.out.println("Production data initialized successfully!");
    }
}