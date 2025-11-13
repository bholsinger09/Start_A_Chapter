package com.turningpoint.chapterorganizer.dto;

import com.turningpoint.chapterorganizer.entity.MemberRole;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable Data Transfer Object for Member information.
 * Follows Robert Martin's Clean Code principle of preferring immutable objects for thread safety.
 * Made final to prevent subclassing which could break immutability.
 */
public final class MemberDTO {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String username;
    private final String phoneNumber;
    private final MemberRole role;
    private final Boolean active;
    private final String major;
    private final String graduationYear;
    
    // Chapter information (flattened to avoid circular references)
    private final Long chapterId;
    private final String chapterName;
    private final String universityName;
    private final String state;
    private final String city;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    // Constructor - all fields are set once and become immutable
    public MemberDTO(Long id, String firstName, String lastName, String email, String username,
                     String phoneNumber, MemberRole role, Boolean active, String major, 
                     String graduationYear, Long chapterId, String chapterName, 
                     String universityName, String state, String city, 
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.active = active;
        this.major = major;
        this.graduationYear = graduationYear;
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.universityName = universityName;
        this.state = state;
        this.city = city;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method for creating from entity - ensures proper immutable construction
    public static MemberDTO from(com.turningpoint.chapterorganizer.entity.Member member) {
        return new MemberDTO(
            member.getId(),
            member.getFirstName(),
            member.getLastName(),
            member.getEmail(),
            member.getUsername(),
            member.getPhoneNumber(),
            member.getRole(),
            member.getActive(),
            member.getMajor(),
            member.getGraduationYear(),
            member.getChapterId(),
            member.getChapterName(),
            member.getChapterUniversity(),
            member.getChapterState(),
            member.getChapterCity(),
            member.getCreatedAt(),
            member.getUpdatedAt()
        );
    }

    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String username;
        private String phoneNumber;
        private MemberRole role;
        private Boolean active;
        private String major;
        private String graduationYear;
        private Long chapterId;
        private String chapterName;
        private String universityName;
        private String state;
        private String city;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder role(MemberRole role) { this.role = role; return this; }
        public Builder active(Boolean active) { this.active = active; return this; }
        public Builder major(String major) { this.major = major; return this; }
        public Builder graduationYear(String graduationYear) { this.graduationYear = graduationYear; return this; }
        public Builder chapterId(Long chapterId) { this.chapterId = chapterId; return this; }
        public Builder chapterName(String chapterName) { this.chapterName = chapterName; return this; }
        public Builder universityName(String universityName) { this.universityName = universityName; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public MemberDTO build() {
            return new MemberDTO(id, firstName, lastName, email, username, phoneNumber,
                               role, active, major, graduationYear, chapterId, chapterName,
                               universityName, state, city, createdAt, updatedAt);
        }
    }

    // Getters only - no setters for immutability
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPhoneNumber() { return phoneNumber; }
    public MemberRole getRole() { return role; }
    public Boolean getActive() { return active; }
    public String getMajor() { return major; }
    public String getGraduationYear() { return graduationYear; }
    public Long getChapterId() { return chapterId; }
    public String getChapterName() { return chapterName; }
    public String getUniversityName() { return universityName; }
    public String getState() { return state; }
    public String getCity() { return city; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return active != null && active;
    }

    // Copy methods for creating modified versions (functional approach)
    public MemberDTO withActive(Boolean active) {
        return new MemberDTO(this.id, this.firstName, this.lastName, this.email, 
                           this.username, this.phoneNumber, this.role, active, 
                           this.major, this.graduationYear, this.chapterId, this.chapterName,
                           this.universityName, this.state, this.city, this.createdAt, this.updatedAt);
    }

    public MemberDTO withRole(MemberRole role) {
        return new MemberDTO(this.id, this.firstName, this.lastName, this.email, 
                           this.username, this.phoneNumber, role, this.active, 
                           this.major, this.graduationYear, this.chapterId, this.chapterName,
                           this.universityName, this.state, this.city, this.createdAt, this.updatedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberDTO memberDTO = (MemberDTO) o;
        return Objects.equals(id, memberDTO.id) &&
               Objects.equals(firstName, memberDTO.firstName) &&
               Objects.equals(lastName, memberDTO.lastName) &&
               Objects.equals(email, memberDTO.email) &&
               Objects.equals(username, memberDTO.username) &&
               Objects.equals(phoneNumber, memberDTO.phoneNumber) &&
               role == memberDTO.role &&
               Objects.equals(active, memberDTO.active) &&
               Objects.equals(major, memberDTO.major) &&
               Objects.equals(graduationYear, memberDTO.graduationYear) &&
               Objects.equals(chapterId, memberDTO.chapterId) &&
               Objects.equals(chapterName, memberDTO.chapterName) &&
               Objects.equals(universityName, memberDTO.universityName) &&
               Objects.equals(state, memberDTO.state) &&
               Objects.equals(city, memberDTO.city) &&
               Objects.equals(createdAt, memberDTO.createdAt) &&
               Objects.equals(updatedAt, memberDTO.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, username, phoneNumber,
                          role, active, major, graduationYear, chapterId, chapterName,
                          universityName, state, city, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "MemberDTO{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", username='" + username + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", role=" + role +
               ", active=" + active +
               ", major='" + major + '\'' +
               ", graduationYear='" + graduationYear + '\'' +
               ", chapterId=" + chapterId +
               ", chapterName='" + chapterName + '\'' +
               ", universityName='" + universityName + '\'' +
               ", state='" + state + '\'' +
               ", city='" + city + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}