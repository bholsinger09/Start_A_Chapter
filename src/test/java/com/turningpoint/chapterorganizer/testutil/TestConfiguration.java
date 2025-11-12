package com.turningpoint.chapterorganizer.testutil;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

/**
 * Test Configuration Utilities
 * 
 * Following JUnit Internals best practices:
 * - Centralized test configuration
 * - Reusable test setup methods
 * - Test data management utilities
 * - Test environment configuration
 */
@TestPropertySource(locations = "classpath:application-test.properties")
public class TestConfiguration {
    
    /**
     * Test Database Configuration
     */
    public static class Database {
        public static final String TEST_URL = "jdbc:h2:mem:testdb";
        public static final String TEST_USER = "sa";
        public static final String TEST_PASSWORD = "";
        public static final String TEST_DRIVER = "org.h2.Driver";
    }
    
    /**
     * Test Data Sets - Predefined collections for different test scenarios
     */
    public static class TestDataSets {
        
        /**
         * Creates a minimal test dataset with one chapter and one member
         */
        public static TestDataSet minimal() {
            Chapter chapter = TestDataBuilder.aChapter().ucla().build();
            Member member = TestDataBuilder.aMember().johnSmith().inChapter(chapter).build();
            
            return new TestDataSet()
                .addChapter(chapter)
                .addMember(member);
        }
        
        /**
         * Creates a complete test dataset with multiple chapters and members
         */
        public static TestDataSet complete() {
            Chapter ucla = TestDataBuilder.aChapter().ucla().withId(1L).build();
            Chapter stanford = TestDataBuilder.aChapter().stanford().withId(2L).build();
            Chapter harvard = TestDataBuilder.aChapter().harvard().withId(3L).build();
            
            TestDataSet dataset = new TestDataSet()
                .addChapter(ucla)
                .addChapter(stanford)  
                .addChapter(harvard);
            
            // UCLA Members
            dataset.addMember(TestDataBuilder.aMember()
                .johnSmith().asPresident().inChapter(ucla).withId(1L).build());
            dataset.addMember(TestDataBuilder.aMember()
                .sarahJohnson().asVicePresident().inChapter(ucla).withId(2L).build());
            dataset.addMember(TestDataBuilder.aMember()
                .withName("Mike", "Davis").asTreasurer().inChapter(ucla).withId(3L).build());
            
            // Stanford Members
            dataset.addMember(TestDataBuilder.aMember()
                .withName("Emily", "Wilson").asPresident().inChapter(stanford).withId(4L).build());
            dataset.addMember(TestDataBuilder.aMember()
                .withName("David", "Brown").asSecretary().inChapter(stanford).withId(5L).build());
            
            // Harvard Members
            dataset.addMember(TestDataBuilder.aMember()
                .withName("Lisa", "Anderson").asPresident().inChapter(harvard).withId(6L).build());
            
            return dataset;
        }
        
        /**
         * Creates a leadership-focused dataset for role-based testing
         */
        public static TestDataSet leadership() {
            Chapter chapter = TestDataBuilder.aChapter().ucla().withId(1L).build();
            
            return new TestDataSet()
                .addChapter(chapter)
                .addMember(TestDataBuilder.aMember().adminUser().asPresident().inChapter(chapter).withId(1L).build())
                .addMember(TestDataBuilder.aMember().sarahJohnson().asVicePresident().inChapter(chapter).withId(2L).build())
                .addMember(TestDataBuilder.aMember().withName("Tom", "Secretary").asSecretary().inChapter(chapter).withId(3L).build())
                .addMember(TestDataBuilder.aMember().withName("Ann", "Treasurer").asTreasurer().inChapter(chapter).withId(4L).build());
        }
        
        /**
         * Creates a dataset for testing concurrent operations
         */
        public static TestDataSet concurrency() {
            Chapter chapter = TestDataBuilder.aChapter().ucla().withId(1L).build();
            TestDataSet dataset = new TestDataSet().addChapter(chapter);
            
            // Add multiple members for concurrent testing
            for (int i = 1; i <= 10; i++) {
                Member member = TestDataBuilder.aMember()
                    .withName("User" + i, "Test")
                    .withEmail("user" + i + "@test.com")
                    .withUsername("user" + i)
                    .inChapter(chapter)
                    .withId((long) i)
                    .build();
                dataset.addMember(member);
            }
            
            return dataset;
        }
    }
    
    /**
     * Test Data Container
     */
    public static class TestDataSet {
        private final List<Chapter> chapters = new ArrayList<>();
        private final List<Member> members = new ArrayList<>();
        
        public TestDataSet addChapter(Chapter chapter) {
            chapters.add(chapter);
            return this;
        }
        
        public TestDataSet addMember(Member member) {
            members.add(member);
            return this;
        }
        
        public List<Chapter> getChapters() {
            return new ArrayList<>(chapters);
        }
        
        public List<Member> getMembers() {
            return new ArrayList<>(members);
        }
        
        public Chapter getFirstChapter() {
            return chapters.isEmpty() ? null : chapters.get(0);
        }
        
        public Member getFirstMember() {
            return members.isEmpty() ? null : members.get(0);
        }
        
        public List<Member> getMembersInChapter(Chapter chapter) {
            return members.stream()
                .filter(member -> member.getChapter().getId().equals(chapter.getId()))
                .toList();
        }
        
        public List<Member> getMembersByRole(MemberRole role) {
            return members.stream()
                .filter(member -> member.getRole() == role)
                .toList();
        }
    }
    
    /**
     * Test Timing Utilities
     */
    public static class Timing {
        public static final long FAST_TEST_TIMEOUT_MS = 1000;
        public static final long SLOW_TEST_TIMEOUT_MS = 5000;
        public static final long INTEGRATION_TEST_TIMEOUT_MS = 10000;
        
        /**
         * Executes code and measures execution time
         */
        public static long measureExecutionTime(Runnable code) {
            long startTime = System.currentTimeMillis();
            code.run();
            return System.currentTimeMillis() - startTime;
        }
    }
    
    /**
     * Test Validation Utilities
     */
    public static class Validation {
        
        /**
         * Validates that a test member was created using our refactored methods
         */
        public static boolean wasCreatedByRefactoredMethod(Member member) {
            return member != null &&
                   member.getFirstName() != null && !member.getFirstName().trim().isEmpty() &&
                   member.getLastName() != null && !member.getLastName().trim().isEmpty() &&
                   member.getEmail() != null && member.getEmail().contains("@") &&
                   member.getUsername() != null && !member.getUsername().trim().isEmpty() &&
                   member.getRole() != null &&
                   member.getChapter() != null;
        }
        
        /**
         * Validates that a test chapter was created using our refactored methods
         */
        public static boolean wasCreatedByRefactoredMethod(Chapter chapter) {
            return chapter != null &&
                   chapter.getName() != null && !chapter.getName().trim().isEmpty() &&
                   chapter.getUniversityName() != null && !chapter.getUniversityName().trim().isEmpty() &&
                   chapter.getCity() != null && !chapter.getCity().trim().isEmpty() &&
                   chapter.getState() != null && !chapter.getState().trim().isEmpty() &&
                   chapter.getActive() != null && chapter.getActive();
        }
    }
}