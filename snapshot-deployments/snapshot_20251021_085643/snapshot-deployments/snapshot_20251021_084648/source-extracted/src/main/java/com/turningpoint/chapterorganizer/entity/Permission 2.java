package com.turningpoint.chapterorganizer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "idx_permission_name", columnList = "name"),
    @Index(name = "idx_permission_resource", columnList = "resource"),
    @Index(name = "idx_permission_action", columnList = "action")
})
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Permission name cannot be blank")
    @Size(max = 100, message = "Permission name cannot exceed 100 characters")
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    
    @NotBlank(message = "Resource cannot be blank")
    @Size(max = 50, message = "Resource cannot exceed 50 characters")
    @Column(name = "resource", nullable = false)
    private String resource; // e.g., "chapter", "event", "member"
    
    @NotBlank(message = "Action cannot be blank")
    @Size(max = 50, message = "Action cannot exceed 50 characters")
    @Column(name = "action", nullable = false)
    private String action; // e.g., "create", "read", "update", "delete", "manage"
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Column(name = "description")
    private String description;
    
    @Column(name = "is_system_permission", nullable = false)
    private Boolean isSystemPermission = false; // System permissions cannot be deleted
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();
    
    public Permission() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Permission(String name, String resource, String action, String description) {
        this();
        this.name = name;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }
    
    public Permission(String name, String resource, String action, String description, Boolean isSystemPermission) {
        this(name, resource, action, description);
        this.isSystemPermission = isSystemPermission;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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
    
    public String getResource() {
        return resource;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsSystemPermission() {
        return isSystemPermission;
    }
    
    public void setIsSystemPermission(Boolean isSystemPermission) {
        this.isSystemPermission = isSystemPermission;
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
    
    public Set<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    // Helper methods
    public String getFullPermissionName() {
        return resource + ":" + action;
    }
    
    public boolean isResourceAction(String resource, String action) {
        return this.resource.equals(resource) && this.action.equals(action);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                ", description='" + description + '\'' +
                ", isSystemPermission=" + isSystemPermission +
                '}';
    }
}