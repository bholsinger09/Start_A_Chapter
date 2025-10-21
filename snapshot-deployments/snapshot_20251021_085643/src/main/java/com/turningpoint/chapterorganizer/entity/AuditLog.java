package com.turningpoint.chapterorganizer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Action type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @NotBlank(message = "Entity type is required")
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "user_identifier")
    private String userIdentifier; // Could be email, username, or system identifier

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues; // JSON string of previous values

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues; // JSON string of new values

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "chapter_id")
    private Long chapterId; // For filtering logs by chapter

    @NotNull(message = "Success status is required")
    @Column(nullable = false)
    private Boolean success = true;

    @Column(name = "error_message")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    // Constructors
    public AuditLog() {
    }

    public AuditLog(AuditAction action, String entityType, Long entityId, String userIdentifier) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userIdentifier = userIdentifier;
    }

    // Static factory methods for common audit scenarios
    public static AuditLog createAction(String entityType, Long entityId, String userIdentifier, String newValues) {
        AuditLog log = new AuditLog(AuditAction.CREATE, entityType, entityId, userIdentifier);
        log.setNewValues(newValues);
        log.setDescription("Created new " + entityType.toLowerCase());
        return log;
    }

    public static AuditLog updateAction(String entityType, Long entityId, String userIdentifier, 
                                      String oldValues, String newValues) {
        AuditLog log = new AuditLog(AuditAction.UPDATE, entityType, entityId, userIdentifier);
        log.setOldValues(oldValues);
        log.setNewValues(newValues);
        log.setDescription("Updated " + entityType.toLowerCase());
        return log;
    }

    public static AuditLog deleteAction(String entityType, Long entityId, String userIdentifier, String oldValues) {
        AuditLog log = new AuditLog(AuditAction.DELETE, entityType, entityId, userIdentifier);
        log.setOldValues(oldValues);
        log.setDescription("Deleted " + entityType.toLowerCase());
        return log;
    }

    public static AuditLog loginAction(String userIdentifier, String ipAddress, String userAgent) {
        AuditLog log = new AuditLog(AuditAction.LOGIN, "User", null, userIdentifier);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setDescription("User login");
        return log;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
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

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getOldValues() {
        return oldValues;
    }

    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(id, auditLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", action=" + action +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", userIdentifier='" + userIdentifier + '\'' +
                ", timestamp=" + timestamp +
                ", success=" + success +
                '}';
    }
}