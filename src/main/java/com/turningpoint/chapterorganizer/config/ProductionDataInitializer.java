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
        // Create a few sample chapters for production
        Chapter stanford = new Chapter();
        stanford.setName("Stanford Turning Point");
        stanford.setUniversityName("Stanford University");
        stanford.setState("California");
        stanford.setCity("Stanford");
        stanford.setFoundedDate(LocalDateTime.now().minusMonths(6));
        stanford.setDescription("Turning Point chapter at Stanford University");
        stanford.setWebsiteUrl("https://turningpoint.stanford.edu");
        stanford.setContactEmail("contact@turningpoint.stanford.edu");
        stanford.setIsActive(true);
        stanford.setCurrentMemberCount(25);

        Chapter berkeley = new Chapter();
        berkeley.setName("UC Berkeley Turning Point");
        berkeley.setUniversityName("University of California, Berkeley");
        berkeley.setState("California");
        berkeley.setCity("Berkeley");
        berkeley.setFoundedDate(LocalDateTime.now().minusMonths(4));
        berkeley.setDescription("Turning Point chapter at UC Berkeley");
        berkeley.setWebsiteUrl("https://turningpoint.berkeley.edu");
        berkeley.setContactEmail("contact@turningpoint.berkeley.edu");
        berkeley.setIsActive(true);
        berkeley.setCurrentMemberCount(18);

        // Save chapters
        stanford = chapterRepository.save(stanford);
        berkeley = chapterRepository.save(berkeley);

        // Create sample members
        Member member1 = new Member();
        member1.setFirstName("John");
        member1.setLastName("Smith");
        member1.setEmail("john.smith@stanford.edu");
        member1.setRole(MemberRole.PRESIDENT);
        member1.setJoinedDate(LocalDateTime.now().minusMonths(3));
        member1.setIsActive(true);
        member1.setChapter(stanford);

        Member member2 = new Member();
        member2.setFirstName("Jane");
        member2.setLastName("Doe");
        member2.setEmail("jane.doe@berkeley.edu");
        member2.setRole(MemberRole.PRESIDENT);
        member2.setJoinedDate(LocalDateTime.now().minusMonths(2));
        member2.setIsActive(true);
        member2.setChapter(berkeley);

        memberRepository.saveAll(Arrays.asList(member1, member2));

        // Create sample events
        Event event1 = new Event();
        event1.setName("Welcome Meeting");
        event1.setDescription("Welcome new members to our chapter");
        event1.setEventType(EventType.MEETING);
        event1.setEventDate(LocalDateTime.now().plusDays(7));
        event1.setLocation("Student Center Room 101");
        event1.setChapter(stanford);

        Event event2 = new Event();
        event2.setName("Community Service Day");
        event2.setDescription("Volunteer at local food bank");
        event2.setEventType(EventType.SERVICE);
        event2.setEventDate(LocalDateTime.now().plusDays(14));
        event2.setLocation("Berkeley Food Bank");
        event2.setChapter(berkeley);

        eventRepository.saveAll(Arrays.asList(event1, event2));

        System.out.println("Production data initialized successfully!");
    }
}