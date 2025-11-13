package com.turningpoint.chapterorganizer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "chapters", 
    indexes = {
        @Index(name = "idx_chapter_name", columnList = "name"),
        @Index(name = "idx_chapter_university", columnList = "universityName"),
        @Index(name = "idx_chapter_state", columnList = "state"),
        @Index(name = "idx_chapter_active", columnList = "active"),
        @Index(name = "idx_chapter_state_active", columnList = "state, active")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_chapter_name_university", 
                         columnNames = {"name", "universityName"})
    }
)
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Chapter name is required")
    @Size(min = 2, max = 100, message = "Chapter name must be between 2 and 100 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "University name is required")
    @Size(min = 2, max = 150, message = "University name must be between 2 and 150 characters")
    @Column(nullable = false)
    private String universityName;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    @Column(nullable = false)
    private String state;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    @Column(nullable = false)
    private String city;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    private String description;

    @NotNull(message = "Active status is required")
    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties({"chapter"})
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties({"chapter"})
    private List<Event> events = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Chapter() {
    }

    public Chapter(String name, String universityName, String state, String city) {
        this.name = name;
        this.universityName = universityName;
        this.state = state;
        this.city = city;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Rich domain behavior - transforming anemic model into behavioral object
    public void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        if (!this.active) {
            throw new IllegalStateException("Cannot add member to inactive chapter");
        }
        members.add(member);
        member.setChapter(this);
    }

    public void removeMember(Member member) {
        if (member == null) {
            return; // Idempotent operation
        }
        members.remove(member);
        member.setChapter(null);
    }

    public void addEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (!this.active) {
            throw new IllegalStateException("Cannot add event to inactive chapter");
        }
        events.add(event);
        event.setChapter(this);
    }

    public void removeEvent(Event event) {
        if (event == null) {
            return; // Idempotent operation
        }
        events.remove(event);
        event.setChapter(null);
    }

    // Business behavior methods
    public int getMemberCount() {
        return members != null ? members.size() : 0;
    }

    public int getActiveMemberCount() {
        if (members == null) return 0;
        return (int) members.stream()
                .filter(Member::getActive)
                .count();
    }

    public List<Member> getLeadershipMembers() {
        if (members == null) return new ArrayList<>();
        return members.stream()
                .filter(Member::isLeader)
                .filter(Member::getActive)
                .toList();
    }

    public boolean hasPresident() {
        return members != null && members.stream()
                .anyMatch(m -> m.getActive() && m.getRole() == MemberRole.PRESIDENT);
    }

    public Optional<Member> getPresident() {
        if (members == null) return Optional.empty();
        return members.stream()
                .filter(m -> m.getActive() && m.getRole() == MemberRole.PRESIDENT)
                .findFirst();
    }

    public boolean canAcceptNewMembers() {
        return this.active && getMemberCount() < 100; // Business rule: max 100 members
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public String getFullLocation() {
        return city + ", " + state;
    }

    // equals and hashCode based on business key (name + universityName)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(name, chapter.name) &&
                Objects.equals(universityName, chapter.universityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, universityName);
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", universityName='" + universityName + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", active=" + active +
                '}';
    }
}