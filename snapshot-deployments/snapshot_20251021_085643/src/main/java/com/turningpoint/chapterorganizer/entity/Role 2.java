package com.turningpoint.chapterorganizer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_role_name", columnList = "name"),
    @Index(name = "idx_role_level", columnList = "hierarchyLevel")
})
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 100, message = "Role name cannot exceed 100 characters")
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Column(name = "description")
    private String description;
    
    @Column(name = "hierarchy_level", nullable = false)
    private Integer hierarchyLevel = 0; // Higher numbers = higher authority
    
    @Column(name = "is_system_role", nullable = false)
    private Boolean isSystemRole = false; // System roles cannot be deleted
    
    @Column(name = "is_assignable", nullable = false)
    private Boolean isAssignable = true; // Can this role be assigned to users
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"),
        indexes = {
            @Index(name = "idx_role_permissions_role", columnList = "role_id"),
            @Index(name = "idx_role_permissions_permission", columnList = "permission_id")
        }
    )
    private Set<Permission> permissions = new HashSet<>();
    
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();
    
    public Role() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Role(String name, String description, Integer hierarchyLevel) {
        this();
        this.name = name;
        this.description = description;
        this.hierarchyLevel = hierarchyLevel;
    }
    
    public Role(String name, String description, Integer hierarchyLevel, Boolean isSystemRole) {
        this(name, description, hierarchyLevel);
        this.isSystemRole = isSystemRole;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }
    
    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }
    
    public Boolean getIsSystemRole() {
        return isSystemRole;
    }
    
    public void setIsSystemRole(Boolean isSystemRole) {
        this.isSystemRole = isSystemRole;
    }
    
    public Boolean getIsAssignable() {
        return isAssignable;
    }
    
    public void setIsAssignable(Boolean isAssignable) {
        this.isAssignable = isAssignable;
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
    
    public Set<Permission> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    
    public Set<UserRole> getUserRoles() {
        return userRoles;
    }
    
    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
    
    // Helper methods
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
        permission.getRoles().add(this);
    }
    
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
        permission.getRoles().remove(this);
    }
    
    public boolean hasPermission(String resource, String action) {
        return permissions.stream()
                .anyMatch(permission -> permission.isResourceAction(resource, action));
    }
    
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
    
    public Set<String> getPermissionNames() {
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
    
    public boolean hasHigherAuthorityThan(Role otherRole) {
        return this.hierarchyLevel > otherRole.getHierarchyLevel();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", hierarchyLevel=" + hierarchyLevel +
                ", isSystemRole=" + isSystemRole +
                ", isAssignable=" + isAssignable +
                '}';
    }
}