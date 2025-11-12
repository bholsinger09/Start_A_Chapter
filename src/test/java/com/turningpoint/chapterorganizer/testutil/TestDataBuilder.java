package com.turningpoint.chapterorganizer.testutil;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;

/**
 * Test Data Builder implementing Object Mother Pattern
 * 
 * Following JUnit Internals best practices:
 * - Fluent interface for readable test setup
 * - Sensible defaults to minimize test noise
 * - Builder pattern for flexible object creation
 * - Immutable test data creation
 */
public class TestDataBuilder {
    
    /**
     * Chapter Builder - Creates test chapters with fluent interface
     */
    public static class ChapterBuilder {
        private String name = "Default Chapter";
        private String universityName = "Default University";
        private String city = "Default City";
        private String state = "CA";
        private boolean active = true;
        private Long id;
        
        public static ChapterBuilder aChapter() {
            return new ChapterBuilder();
        }
        
        public ChapterBuilder withName(String name) {
            this.name = name;
            return this;
        }
        
        public ChapterBuilder atUniversity(String universityName) {
            this.universityName = universityName;
            return this;
        }
        
        public ChapterBuilder inCity(String city) {
            this.city = city;
            return this;
        }
        
        public ChapterBuilder inState(String state) {
            this.state = state;
            return this;
        }
        
        public ChapterBuilder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public ChapterBuilder inactive() {
            this.active = false;
            return this;
        }
        
        public ChapterBuilder active() {
            this.active = true;
            return this;
        }
        
        // Common test scenarios
        public ChapterBuilder ucla() {
            return withName("UCLA")
                .atUniversity("University of California, Los Angeles")
                .inCity("Los Angeles")
                .inState("CA");
        }
        
        public ChapterBuilder stanford() {
            return withName("Stanford")
                .atUniversity("Stanford University")
                .inCity("Stanford")
                .inState("CA");
        }
        
        public ChapterBuilder harvard() {
            return withName("Harvard")
                .atUniversity("Harvard University")
                .inCity("Cambridge")
                .inState("MA");
        }
        
        public Chapter build() {
            Chapter chapter = new Chapter(name, universityName, city, state);
            if (id != null) {
                chapter.setId(id);
            }
            chapter.setActive(active);
            return chapter;
        }
    }
    
    /**
     * Member Builder - Creates test members with fluent interface
     */
    public static class MemberBuilder {
        private String firstName = "John";
        private String lastName = "Doe";
        private String email = "john.doe@test.com";
        private String username = "johndoe";
        private String phoneNumber = "555-0123";
        private MemberRole role = MemberRole.MEMBER;
        private String major = "Computer Science";
        private String graduationYear = "2024";
        private String password = "password123";
        private boolean active = true;
        private Chapter chapter = ChapterBuilder.aChapter().build();
        private Long id;
        
        public static MemberBuilder aMember() {
            return new MemberBuilder();
        }
        
        public MemberBuilder withName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            return this;
        }
        
        public MemberBuilder withEmail(String email) {
            this.email = email;
            return this;
        }
        
        public MemberBuilder withUsername(String username) {
            this.username = username;
            return this;
        }
        
        public MemberBuilder withPhone(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
        
        public MemberBuilder withRole(MemberRole role) {
            this.role = role;
            return this;
        }
        
        public MemberBuilder withMajor(String major) {
            this.major = major;
            return this;
        }
        
        public MemberBuilder graduatingIn(String year) {
            this.graduationYear = year;
            return this;
        }
        
        public MemberBuilder withPassword(String password) {
            this.password = password;
            return this;
        }
        
        public MemberBuilder inChapter(Chapter chapter) {
            this.chapter = chapter;
            return this;
        }
        
        public MemberBuilder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public MemberBuilder inactive() {
            this.active = false;
            return this;
        }
        
        // Role-specific builders
        public MemberBuilder asPresident() {
            return withRole(MemberRole.PRESIDENT);
        }
        
        public MemberBuilder asVicePresident() {
            return withRole(MemberRole.VICE_PRESIDENT);
        }
        
        public MemberBuilder asSecretary() {
            return withRole(MemberRole.SECRETARY);
        }
        
        public MemberBuilder asTreasurer() {
            return withRole(MemberRole.TREASURER);
        }
        
        public MemberBuilder asMember() {
            return withRole(MemberRole.MEMBER);
        }
        
        // Common test personas
        public MemberBuilder johnSmith() {
            return withName("John", "Smith")
                .withEmail("john.smith@test.com")
                .withUsername("johnsmith");
        }
        
        public MemberBuilder sarahJohnson() {
            return withName("Sarah", "Johnson")
                .withEmail("sarah.johnson@test.com")
                .withUsername("sarahj")
                .asVicePresident();
        }
        
        public MemberBuilder adminUser() {
            return withName("Admin", "User")
                .withEmail("admin@test.com")
                .withUsername("admin")
                .asPresident();
        }
        
        public Member build() {
            Member member = new Member(firstName, lastName, email, chapter);
            if (id != null) {
                member.setId(id);
            }
            member.setUsername(username);
            member.setPhoneNumber(phoneNumber);
            member.setRole(role);
            member.setMajor(major);
            member.setGraduationYear(graduationYear);
            member.setPassword(password);
            member.setActive(active);
            return member;
        }
    }
    
    // Static factory methods for convenience
    public static ChapterBuilder aChapter() {
        return ChapterBuilder.aChapter();
    }
    
    public static MemberBuilder aMember() {
        return MemberBuilder.aMember();
    }
    
    // Common test scenarios
    public static Chapter uclaChapter() {
        return aChapter().ucla().build();
    }
    
    public static Chapter stanfordChapter() {
        return aChapter().stanford().build();
    }
    
    public static Member typicalMember() {
        return aMember().build();
    }
    
    public static Member presidentMember() {
        return aMember().asPresident().build();
    }
    
    public static Member memberInChapter(Chapter chapter) {
        return aMember().inChapter(chapter).build();
    }
}