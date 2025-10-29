package com.turningpoint.chapterorganizer.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

@Entity
@Table(name = "chapters")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Chapter name is required")
    @Size(min = 2, max = 100, message = "Chapter name must be between 2 and 100 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "chapters"})
    private Institution institution;

    // Legacy fields for backward compatibility - will be deprecated
    @Column(name = "university_name")
    private String universityName;

    @Column(name = "state")
    private String state;

    @Column(name = "city")
    private String city;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    private String description;

    @Column(name = "founded_date")
    private LocalDateTime foundedDate;

    @NotNull(message = "Active status is required")
    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("chapter-members")
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("chapter-events")
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

    public Chapter(String name, Institution institution) {
        this.name = name;
        this.institution = institution;
        // Set legacy fields for backward compatibility
        if (institution != null) {
            this.universityName = institution.getName();
            this.state = institution.getState();
            this.city = institution.getCity();
        }
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

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        // Update legacy fields for backward compatibility
        if (institution != null) {
            this.universityName = institution.getName();
            this.state = institution.getState();
            this.city = institution.getCity();
        }
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
    
    public String getLocation() {
        if (city != null && state != null) {
            return city + ", " + state;
        } else if (city != null) {
            return city;
        } else if (state != null) {
            return state;
        }
        return "Unknown Location";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getFoundedDate() {
        return foundedDate;
    }

    public void setFoundedDate(LocalDateTime foundedDate) {
        this.foundedDate = foundedDate;
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

    public int getMemberCount() {
        return members != null ? members.size() : 0;
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

    // Helper methods for managing relationships
    public void addMember(Member member) {
        members.add(member);
        member.setChapter(this);
    }

    public void removeMember(Member member) {
        members.remove(member);
        member.setChapter(null);
    }

    public void addEvent(Event event) {
        events.add(event);
        event.setChapter(this);
    }

    public void removeEvent(Event event) {
        events.remove(event);
        event.setChapter(null);
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