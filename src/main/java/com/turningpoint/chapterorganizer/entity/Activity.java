package com.turningpoint.chapterorganizer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;
    
    @Column(nullable = false)
    private String description;
    
    @Column(name = "entity_type")
    private String entityType; // "Member", "Event", "Blog", "Chapter"
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member user; // Who performed the action
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional data
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityPriority priority = ActivityPriority.NORMAL;
    
    // Constructors
    public Activity() {
        this.priority = ActivityPriority.NORMAL;
    }
    
    // Constructor for easy creation
    public Activity(ActivityType type, String description, Member user, Chapter chapter) {
        this.type = type;
        this.description = description;
        this.user = user;
        this.chapter = chapter;
        this.priority = ActivityPriority.NORMAL;
    }
    
    public Activity(ActivityType type, String description, String entityType, Long entityId, Member user, Chapter chapter) {
        this.type = type;
        this.description = description;
        this.entityType = entityType;
        this.entityId = entityId;
        this.user = user;
        this.chapter = chapter;
        this.priority = ActivityPriority.NORMAL;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Member getUser() {
        return user;
    }

    public void setUser(Member user) {
        this.user = user;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ActivityPriority getPriority() {
        return priority;
    }

    public void setPriority(ActivityPriority priority) {
        this.priority = priority;
    }
}