package com.turningpoint.chapterorganizer.security.context;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.Permission;
import com.turningpoint.chapterorganizer.entity.Role;
import com.turningpoint.chapterorganizer.entity.UserRole;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Security context holding information about the current authenticated user.
 */
public class SecurityContext {
    
    private final Member currentUser;
    private final Set<UserRole> activeUserRoles;
    private final Set<Permission> effectivePermissions;
    private final Set<Role> effectiveRoles;
    
    public SecurityContext(Member currentUser, Set<UserRole> activeUserRoles) {
        this.currentUser = currentUser;
        this.activeUserRoles = activeUserRoles;
        this.effectiveRoles = activeUserRoles.stream()
                .filter(UserRole::isEffective)
                .map(UserRole::getRole)
                .collect(Collectors.toSet());
        this.effectivePermissions = effectiveRoles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .collect(Collectors.toSet());
    }
    
    public Member getCurrentUser() {
        return currentUser;
    }
    
    public Set<UserRole> getActiveUserRoles() {
        return activeUserRoles;
    }
    
    public Set<Permission> getEffectivePermissions() {
        return effectivePermissions;
    }
    
    public Set<Role> getEffectiveRoles() {
        return effectiveRoles;
    }
    
    /**
     * Check if user has a specific permission
     */
    public boolean hasPermission(String resource, String action) {
        return effectivePermissions.stream()
                .anyMatch(permission -> permission.isResourceAction(resource, action));
    }
    
    /**
     * Check if user has a specific permission by name
     */
    public boolean hasPermission(String permissionName) {
        return effectivePermissions.stream()
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }
    
    /**
     * Check if user has any of the specified permissions
     */
    public boolean hasAnyPermission(String... permissionNames) {
        for (String permissionName : permissionNames) {
            if (hasPermission(permissionName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if user has all of the specified permissions
     */
    public boolean hasAllPermissions(String... permissionNames) {
        for (String permissionName : permissionNames) {
            if (!hasPermission(permissionName)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if user has a specific role
     */
    public boolean hasRole(String roleName) {
        return effectiveRoles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }
    
    /**
     * Check if user has any of the specified roles
     */
    public boolean hasAnyRole(String... roleNames) {
        for (String roleName : roleNames) {
            if (hasRole(roleName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if user has all of the specified roles
     */
    public boolean hasAllRoles(String... roleNames) {
        for (String roleName : roleNames) {
            if (!hasRole(roleName)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if user has a role with minimum hierarchy level
     */
    public boolean hasMinimumHierarchyLevel(int minLevel) {
        return effectiveRoles.stream()
                .anyMatch(role -> role.getHierarchyLevel() >= minLevel);
    }
    
    /**
     * Get the highest hierarchy level of user's roles
     */
    public int getHighestHierarchyLevel() {
        return effectiveRoles.stream()
                .mapToInt(Role::getHierarchyLevel)
                .max()
                .orElse(0);
    }
    
    /**
     * Check if user has chapter-scoped permission
     */
    public boolean hasChapterPermission(Long chapterId, String resource, String action) {
        return activeUserRoles.stream()
                .filter(UserRole::isEffective)
                .filter(userRole -> userRole.getChapter() != null && 
                                   userRole.getChapter().getId().equals(chapterId))
                .flatMap(userRole -> userRole.getRole().getPermissions().stream())
                .anyMatch(permission -> permission.isResourceAction(resource, action));
    }
    
    /**
     * Check if user has global permission
     */
    public boolean hasGlobalPermission(String resource, String action) {
        return activeUserRoles.stream()
                .filter(UserRole::isEffective)
                .filter(UserRole::isGlobal)
                .flatMap(userRole -> userRole.getRole().getPermissions().stream())
                .anyMatch(permission -> permission.isResourceAction(resource, action));
    }
    
    /**
     * Check if user has chapter-scoped role
     */
    public boolean hasChapterRole(Long chapterId, String roleName) {
        return activeUserRoles.stream()
                .filter(UserRole::isEffective)
                .filter(userRole -> userRole.getChapter() != null && 
                                   userRole.getChapter().getId().equals(chapterId))
                .anyMatch(userRole -> userRole.getRole().getName().equals(roleName));
    }
    
    /**
     * Check if user has global role
     */
    public boolean hasGlobalRole(String roleName) {
        return activeUserRoles.stream()
                .filter(UserRole::isEffective)
                .filter(UserRole::isGlobal)
                .anyMatch(userRole -> userRole.getRole().getName().equals(roleName));
    }
    
    /**
     * Check if user is system administrator
     */
    public boolean isSystemAdmin() {
        return hasGlobalRole("Super Administrator") || hasGlobalRole("System Administrator");
    }
    
    /**
     * Check if user is chapter administrator for specific chapter
     */
    public boolean isChapterAdmin(Long chapterId) {
        return hasChapterRole(chapterId, "Chapter President") ||
               hasChapterRole(chapterId, "Chapter Vice President") ||
               isSystemAdmin();
    }
    
    /**
     * Get user's role names
     */
    public Set<String> getRoleNames() {
        return effectiveRoles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
    
    /**
     * Get user's permission names
     */
    public Set<String> getPermissionNames() {
        return effectivePermissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}