package com.turningpoint.chapterorganizer.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable Data Transfer Object for Chapter information.
 * Follows Robert Martin's Clean Code principle of preferring immutable objects for thread safety.
 */
public final class ChapterDto {
    private final Long id;
    private final String name;
    private final String universityName;
    private final String state;
    private final String city;
    private final String description;
    private final Boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<Long> memberIds;

    public ChapterDto(Long id, String name, String universityName, String state, String city, 
                     String description, Boolean active, LocalDateTime createdAt, 
                     LocalDateTime updatedAt, List<Long> memberIds) {
        this.id = id;
        this.name = name;
        this.universityName = universityName;
        this.state = state;
        this.city = city;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        // Create immutable copy of member IDs
        this.memberIds = memberIds != null ? Collections.unmodifiableList(List.copyOf(memberIds)) : Collections.emptyList();
    }

    // Factory method for creating from entity
    public static ChapterDto from(com.turningpoint.chapterorganizer.entity.Chapter chapter) {
        List<Long> memberIds = chapter.getMembers() != null ? 
            chapter.getMembers().stream()
                .map(member -> member.getId())
                .toList() : Collections.emptyList();
                
        return new ChapterDto(
            chapter.getId(),
            chapter.getName(),
            chapter.getUniversityName(),
            chapter.getState(),
            chapter.getCity(),
            chapter.getDescription(),
            chapter.getActive(),
            chapter.getCreatedAt(),
            chapter.getUpdatedAt(),
            memberIds
        );
    }

    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String universityName;
        private String state;
        private String city;
        private String description;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<Long> memberIds;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder universityName(String universityName) {
            this.universityName = universityName;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder memberIds(List<Long> memberIds) {
            this.memberIds = memberIds;
            return this;
        }

        public ChapterDto build() {
            return new ChapterDto(id, name, universityName, state, city, description, 
                                active, createdAt, updatedAt, memberIds);
        }
    }

    // Getters only - no setters for immutability
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUniversityName() {
        return universityName;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    // Copy method for creating modified versions
    public ChapterDto withActive(Boolean active) {
        return new ChapterDto(this.id, this.name, this.universityName, this.state, 
                             this.city, this.description, active, this.createdAt, 
                             this.updatedAt, this.memberIds);
    }

    public ChapterDto withDescription(String description) {
        return new ChapterDto(this.id, this.name, this.universityName, this.state, 
                             this.city, description, this.active, this.createdAt, 
                             this.updatedAt, this.memberIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChapterDto that = (ChapterDto) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(universityName, that.universityName) &&
               Objects.equals(state, that.state) &&
               Objects.equals(city, that.city) &&
               Objects.equals(description, that.description) &&
               Objects.equals(active, that.active) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt) &&
               Objects.equals(memberIds, that.memberIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, universityName, state, city, description, 
                          active, createdAt, updatedAt, memberIds);
    }

    @Override
    public String toString() {
        return "ChapterDto{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", universityName='" + universityName + '\'' +
               ", state='" + state + '\'' +
               ", city='" + city + '\'' +
               ", description='" + description + '\'' +
               ", active=" + active +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               ", memberIds=" + memberIds +
               '}';
    }
}