package com.turningpoint.chapterorganizer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_roles", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id", "chapter_id"}),
       indexes = {
           @Index(name = "idx_user_roles_user", columnList = "user_id"),
           @Index(name = "idx_user_roles_role", columnList = "role_id"),
           @Index(name = "idx_user_roles_chapter", columnList = "chapter_id"),
           @Index(name = "idx_user_roles_active", columnList = "is_active")
       })
public class UserRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user; // Using Member as our user entity
    
    @NotNull(message = "Role is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter; // Null for global roles, specific chapter for chapter-scoped roles
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by")
    private Member grantedBy;
    
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revoked_by")
    private Member revokedBy;
    
    @Column(name = "revocation_reason")
    private String revocationReason;
    
    public UserRole() {
        this.grantedAt = LocalDateTime.now();
    }
    
    public UserRole(Member user, Role role) {
        this();
        this.user = user;
        this.role = role;
    }
    
    public UserRole(Member user, Role role, Chapter chapter) {
        this(user, role);
        this.chapter = chapter;
    }
    
    public UserRole(Member user, Role role, Chapter chapter, Member grantedBy) {
        this(user, role, chapter);
        this.grantedBy = grantedBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Member getUser() {
        return user;
    }
    
    public void setUser(Member user) {
        this.user = user;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Chapter getChapter() {
        return chapter;
    }
    
    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }
    
    public void setGrantedAt(LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public Member getGrantedBy() {
        return grantedBy;
    }
    
    public void setGrantedBy(Member grantedBy) {
        this.grantedBy = grantedBy;
    }
    
    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }
    
    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }
    
    public Member getRevokedBy() {
        return revokedBy;
    }
    
    public void setRevokedBy(Member revokedBy) {
        this.revokedBy = revokedBy;
    }
    
    public String getRevocationReason() {
        return revocationReason;
    }
    
    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }
    
    // Helper methods
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isRevoked() {
        return revokedAt != null;
    }
    
    public boolean isEffective() {
        return isActive && !isExpired() && !isRevoked();
    }
    
    public boolean isGlobal() {
        return chapter == null;
    }
    
    public boolean isChapterScoped() {
        return chapter != null;
    }
    
    public void revoke(Member revokedBy, String reason) {
        this.isActive = false;
        this.revokedAt = LocalDateTime.now();
        this.revokedBy = revokedBy;
        this.revocationReason = reason;
    }
    
    public void activate() {
        this.isActive = true;
        this.revokedAt = null;
        this.revokedBy = null;
        this.revocationReason = null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(user, userRole.user) &&
               Objects.equals(role, userRole.role) &&
               Objects.equals(chapter, userRole.chapter);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(user, role, chapter);
    }
    
    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", user=" + (user != null ? user.getEmail() : null) +
                ", role=" + (role != null ? role.getName() : null) +
                ", chapter=" + (chapter != null ? chapter.getName() : "GLOBAL") +
                ", isActive=" + isActive +
                ", grantedAt=" + grantedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}