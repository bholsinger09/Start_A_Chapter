package com.turningpoint.chapterorganizer.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "event_rsvps", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "member_id"}))
public class EventRSVP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Event is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference("event-rsvps")
    private Event event;

    @NotNull(message = "Member is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonBackReference("member-rsvps")
    private Member member;

    @NotNull(message = "RSVP status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RSVPStatus status = RSVPStatus.PENDING;

    @Column(name = "rsvp_date", nullable = false)
    private LocalDateTime rsvpDate = LocalDateTime.now();

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "attended")
    private Boolean attended;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public EventRSVP() {
    }

    public EventRSVP(Event event, Member member, RSVPStatus status) {
        this.event = event;
        this.member = member;
        this.status = status;
        this.rsvpDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public RSVPStatus getStatus() {
        return status;
    }

    public void setStatus(RSVPStatus status) {
        this.status = status;
        this.rsvpDate = LocalDateTime.now(); // Update RSVP date when status changes
    }

    public LocalDateTime getRsvpDate() {
        return rsvpDate;
    }

    public void setRsvpDate(LocalDateTime rsvpDate) {
        this.rsvpDate = rsvpDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getAttended() {
        return attended;
    }

    public void setAttended(Boolean attended) {
        this.attended = attended;
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
        EventRSVP eventRSVP = (EventRSVP) o;
        return Objects.equals(id, eventRSVP.id) &&
               Objects.equals(event, eventRSVP.event) &&
               Objects.equals(member, eventRSVP.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, member);
    }

    @Override
    public String toString() {
        return "EventRSVP{" +
                "id=" + id +
                ", status=" + status +
                ", rsvpDate=" + rsvpDate +
                ", attended=" + attended +
                '}';
    }
}