package com.turningpoint.chapterorganizer.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event title is required")
    @Size(min = 2, max = 150, message = "Event title must be between 2 and 150 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Event date and time is required")
    @Column(name = "event_date_time", nullable = false)
    private LocalDateTime eventDateTime;

    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    @NotNull(message = "Event type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type = EventType.MEETING;

    @Column(name = "max_attendees")
    private Integer maxAttendees;

    @Column(name = "current_attendees")
    private Integer currentAttendees = 0;

    @NotNull(message = "Active status is required")
    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    @JsonBackReference
    private Chapter chapter;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Event() {
    }

    public Event(String title, LocalDateTime eventDateTime, Chapter chapter) {
        this.title = title;
        this.eventDateTime = eventDateTime;
        this.chapter = chapter;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Integer getCurrentAttendees() {
        return currentAttendees;
    }

    public void setCurrentAttendees(Integer currentAttendees) {
        this.currentAttendees = currentAttendees;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
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

    // Helper methods
    public boolean isFull() {
        return maxAttendees != null && currentAttendees >= maxAttendees;
    }

    public boolean canAcceptMoreAttendees() {
        return !isFull();
    }

    public void incrementAttendees() {
        if (canAcceptMoreAttendees()) {
            this.currentAttendees++;
        }
    }

    public void decrementAttendees() {
        if (this.currentAttendees > 0) {
            this.currentAttendees--;
        }
    }

    // equals and hashCode based on business key (title + eventDateTime + chapter)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Event event = (Event) o;
        return Objects.equals(title, event.title) &&
                Objects.equals(eventDateTime, event.eventDateTime) &&
                Objects.equals(chapter, event.chapter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, eventDateTime, chapter);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", eventDateTime=" + eventDateTime +
                ", location='" + location + '\'' +
                ", type=" + type +
                ", active=" + active +
                '}';
    }
}