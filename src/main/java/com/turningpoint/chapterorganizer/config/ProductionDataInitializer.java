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
        // Create sample chapters from different states to showcase state filtering
        Chapter stanford = new Chapter("Stanford Turning Point", "Stanford University", "California", "Stanford");
        stanford.setDescription("Turning Point chapter at Stanford University");
        stanford.setActive(true);

        Chapter berkeley = new Chapter("UC Berkeley Turning Point", "University of California, Berkeley", "California", "Berkeley");
        berkeley.setDescription("Turning Point chapter at UC Berkeley");
        berkeley.setActive(true);

        Chapter texas = new Chapter("UT Austin Turning Point", "University of Texas at Austin", "Texas", "Austin");
        texas.setDescription("Turning Point chapter at UT Austin");
        texas.setActive(true);

        Chapter mit = new Chapter("MIT Turning Point", "Massachusetts Institute of Technology", "Massachusetts", "Cambridge");
        mit.setDescription("Turning Point chapter at MIT");
        mit.setActive(true);

        Chapter nyu = new Chapter("NYU Turning Point", "New York University", "New York", "New York");
        nyu.setDescription("Turning Point chapter at NYU");
        nyu.setActive(true);

        Chapter florida = new Chapter("UF Turning Point", "University of Florida", "Florida", "Gainesville");
        florida.setDescription("Turning Point chapter at University of Florida");
        florida.setActive(true);

        // Save chapters
        stanford = chapterRepository.save(stanford);
        berkeley = chapterRepository.save(berkeley);
        texas = chapterRepository.save(texas);
        mit = chapterRepository.save(mit);
        nyu = chapterRepository.save(nyu);
        florida = chapterRepository.save(florida);

        // Create sample members for each chapter
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

        Member member3 = new Member();
        member3.setFirstName("Mike");
        member3.setLastName("Johnson");
        member3.setEmail("mike.johnson@utexas.edu");
        member3.setRole(MemberRole.PRESIDENT);
        member3.setActive(true);
        member3.setChapter(texas);

        Member member4 = new Member();
        member4.setFirstName("Sarah");
        member4.setLastName("Williams");
        member4.setEmail("sarah.williams@mit.edu");
        member4.setRole(MemberRole.PRESIDENT);
        member4.setActive(true);
        member4.setChapter(mit);

        Member member5 = new Member();
        member5.setFirstName("Alex");
        member5.setLastName("Chen");
        member5.setEmail("alex.chen@nyu.edu");
        member5.setRole(MemberRole.PRESIDENT);
        member5.setActive(true);
        member5.setChapter(nyu);

        Member member6 = new Member();
        member6.setFirstName("Emma");
        member6.setLastName("Davis");
        member6.setEmail("emma.davis@ufl.edu");
        member6.setRole(MemberRole.PRESIDENT);
        member6.setActive(true);
        member6.setChapter(florida);

        memberRepository.saveAll(Arrays.asList(member1, member2, member3, member4, member5, member6));

        // Create sample events for different chapters
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

        Event event3 = new Event();
        event3.setTitle("Campus Outreach");
        event3.setDescription("Recruit new members on campus");
        event3.setType(EventType.RECRUITMENT);
        event3.setEventDateTime(LocalDateTime.now().plusDays(10));
        event3.setLocation("UT Campus Quad");
        event3.setActive(true);
        event3.setChapter(texas);

        Event event4 = new Event();
        event4.setTitle("Study Session");
        event4.setDescription("Group study and networking");
        event4.setType(EventType.MEETING);
        event4.setEventDateTime(LocalDateTime.now().plusDays(5));
        event4.setLocation("MIT Library Conference Room");
        event4.setActive(true);
        event4.setChapter(mit);

        eventRepository.saveAll(Arrays.asList(event1, event2, event3, event4));

        System.out.println("Production data initialized with chapters from 6 different states!");
    }
}