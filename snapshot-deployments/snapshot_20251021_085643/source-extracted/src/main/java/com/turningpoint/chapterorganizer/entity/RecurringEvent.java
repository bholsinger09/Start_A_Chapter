package com.turningpoint.chapterorganizer.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "recurring_events")
public class RecurringEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Base event is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_event_id", nullable = false)
    @JsonBackReference
    private Event baseEvent;

    @NotNull(message = "Recurrence pattern is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrencePattern pattern;

    @Positive(message = "Interval must be positive")
    @Column(name = "recurrence_interval", nullable = false)
    private Integer interval = 1; // Every X days/weeks/months

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "max_occurrences")
    private Integer maxOccurrences;

    @Column(name = "occurrences_created")
    private Integer occurrencesCreated = 0;

    @NotNull(message = "Active status is required")
    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public RecurringEvent() {
    }

    public RecurringEvent(Event baseEvent, RecurrencePattern pattern, Integer interval) {
        this.baseEvent = baseEvent;
        this.pattern = pattern;
        this.interval = interval;
    }

    // Helper methods
    public boolean shouldCreateNextOccurrence() {
        if (!active) return false;
        if (maxOccurrences != null && occurrencesCreated >= maxOccurrences) return false;
        if (endDate != null && LocalDateTime.now().isAfter(endDate)) return false;
        return true;
    }

    public LocalDateTime calculateNextEventDate() {
        LocalDateTime baseDate = baseEvent.getEventDateTime();
        
        switch (pattern) {
            case DAILY:
                return baseDate.plusDays((long) occurrencesCreated * interval);
            case WEEKLY:
                return baseDate.plusWeeks((long) occurrencesCreated * interval);
            case MONTHLY:
                return baseDate.plusMonths((long) occurrencesCreated * interval);
            case YEARLY:
                return baseDate.plusYears((long) occurrencesCreated * interval);
            default:
                return baseDate;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getBaseEvent() {
        return baseEvent;
    }

    public void setBaseEvent(Event baseEvent) {
        this.baseEvent = baseEvent;
    }

    public RecurrencePattern getPattern() {
        return pattern;
    }

    public void setPattern(RecurrencePattern pattern) {
        this.pattern = pattern;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getMaxOccurrences() {
        return maxOccurrences;
    }

    public void setMaxOccurrences(Integer maxOccurrences) {
        this.maxOccurrences = maxOccurrences;
    }

    public Integer getOccurrencesCreated() {
        return occurrencesCreated;
    }

    public void setOccurrencesCreated(Integer occurrencesCreated) {
        this.occurrencesCreated = occurrencesCreated;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecurringEvent that = (RecurringEvent) o;
        return Objects.equals(id, that.id) && Objects.equals(baseEvent, that.baseEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseEvent);
    }

    @Override
    public String toString() {
        return "RecurringEvent{" +
                "id=" + id +
                ", pattern=" + pattern +
                ", interval=" + interval +
                ", occurrencesCreated=" + occurrencesCreated +
                ", active=" + active +
                '}';
    }
}