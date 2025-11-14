package com.turningpoint.chapterorganizer.config;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Data Population Configuration
 * 
 * REFACTORED using Successive Refinement principles:
 * - Extracted methods for specific responsibilities  
 * - Eliminated repetitive member creation code
 * - Clear separation of concerns between chapters and members
 */
@Component
public class DataPopulation implements CommandLineRunner {

    @Autowired
    private ChapterRepository chapterRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        // Use synchronized block to prevent concurrent execution during startup
        synchronized (DataPopulation.class) {
            populateData();
        }
    }

    private void populateData() {
        try {
            // Populate chapters only if none exist
            if (chapterRepository.count() == 0) {
                populateChapters();
            }
        } catch (Exception e) {
            // If chapters already exist due to concurrent execution, log and continue
            System.out.println("Chapters may already exist: " + e.getMessage());
        }
        
        try {
            // Populate members only if none exist
            if (memberRepository.count() == 0) {
                populateMembers();
            }
        } catch (Exception e) {
            // If members already exist due to concurrent execution, log and continue
            System.out.println("Members may already exist: " + e.getMessage());
        }
    }

    /**
     * REFACTORED: Simple chapter population
     * Single responsibility: Create sample chapters
     */
    private void populateChapters() {
        List<Chapter> chapters = Arrays.asList(
            new Chapter("UCLA", "University of California, Los Angeles", "Los Angeles", "CA"),
            new Chapter("Stanford", "Stanford University", "Stanford", "CA"),
            new Chapter("USC", "University of Southern California", "Los Angeles", "CA"),
            new Chapter("Berkeley", "University of California, Berkeley", "Berkeley", "CA"),
            new Chapter("Harvard", "Harvard University", "Cambridge", "MA"),
            new Chapter("NYU", "New York University", "New York", "NY")
        );
        
        chapterRepository.saveAll(chapters);
        System.out.println("Populated database with " + chapters.size() + " chapters!");
    }

    /**
     * REFACTORED using Successive Refinement
     * 
     * BEFORE: 100+ line method with repetitive member creation
     * AFTER: Clean orchestrator with focused helper methods
     */
    private void populateMembers() {
        List<Chapter> chapters = chapterRepository.findAll();
        if (chapters.isEmpty()) {
            return; // No chapters to assign members to
        }
        
        List<Member> members = new ArrayList<>();
        
        // Step 1: Add administrator (extracted method)
        addAdministratorMember(members, chapters.get(0));
        
        // Step 2: Add sample members (extracted method)
        addSampleMembers(members, chapters);
        
        // Step 3: Save all members
        memberRepository.saveAll(members);
        System.out.println("Populated database with " + members.size() + " members!");
    }
    
    /**
     * EXTRACTED METHOD 1: Add administrator member
     * Single responsibility: Create the admin user
     */
    private void addAdministratorMember(List<Member> members, Chapter chapter) {
        Member admin = createMember("Admin", "User", "admin@example.com", 
                                   "admin", "2082841929", MemberRole.PRESIDENT, 
                                   "Computer Science", "2024", "Password123", chapter);
        members.add(admin);
    }
    
    /**
     * EXTRACTED METHOD 2: Add sample members for different chapters
     * Single responsibility: Create sample members for testing
     */
    private void addSampleMembers(List<Member> members, List<Chapter> chapters) {
        // UCLA Members
        if (chapters.size() > 0) {
            Chapter ucla = chapters.get(0);
            members.add(createMember("John", "Smith", "john.smith@ucla.edu", 
                                   "johnsmith", "310-555-0101", MemberRole.PRESIDENT, 
                                   "Computer Science", "2024", "password123", ucla));
            members.add(createMember("Sarah", "Johnson", "sarah.johnson@ucla.edu", 
                                   "sarahj", "310-555-0102", MemberRole.VICE_PRESIDENT, 
                                   "Business Administration", "2025", "password123", ucla));
            members.add(createMember("Mike", "Davis", "mike.davis@ucla.edu", 
                                   "mikedavis", "310-555-0103", MemberRole.TREASURER, 
                                   "Economics", "2024", "password123", ucla));
        }
        
        // Stanford Members  
        if (chapters.size() > 1) {
            Chapter stanford = chapters.get(1);
            members.add(createMember("Emily", "Wilson", "emily.wilson@stanford.edu", 
                                   "emilyw", "650-555-0201", MemberRole.PRESIDENT, 
                                   "Engineering", "2024", "password123", stanford));
            members.add(createMember("David", "Brown", "david.brown@stanford.edu", 
                                   "davidb", "650-555-0202", MemberRole.SECRETARY, 
                                   "Psychology", "2025", "password123", stanford));
        }
        
        // USC Members
        if (chapters.size() > 2) {
            Chapter usc = chapters.get(2);
            members.add(createMember("Jessica", "Martinez", "jessica.martinez@usc.edu", 
                                   "jessicam", "213-555-0301", MemberRole.PRESIDENT, 
                                   "Film Studies", "2024", "password123", usc));
            members.add(createMember("Alex", "Garcia", "alex.garcia@usc.edu", 
                                   "alexg", "213-555-0302", MemberRole.MEMBER, 
                                   "International Relations", "2026", "password123", usc));
        }
        
        // Berkeley Members
        if (chapters.size() > 3) {
            Chapter berkeley = chapters.get(3);
            members.add(createMember("Rachel", "Kim", "rachel.kim@berkeley.edu", 
                                   "rachelk", "510-555-0401", MemberRole.PRESIDENT, 
                                   "Chemical Engineering", "2024", "password123", berkeley));
        }
    }
    
    /**
     * EXTRACTED HELPER METHOD: Create member with all properties
     * Single responsibility: Build Member object with consistent pattern
     * 
     * Benefits of extraction:
     * - Eliminates 10+ lines of repetitive setter calls per member
     * - Consistent member creation pattern
     * - Single place to modify member creation logic
     * - Easier testing and validation
     */
    private Member createMember(String firstName, String lastName, String email, 
                               String username, String phone, MemberRole role,
                               String major, String graduationYear, String password, Chapter chapter) {
        Member member = new Member(firstName, lastName, email, chapter);
        member.setUsername(username);
        member.setPhoneNumber(phone);
        member.setRole(role);
        member.setMajor(major);
        member.setGraduationYear(graduationYear);
        member.setPassword(password);
        return member;
    }
}