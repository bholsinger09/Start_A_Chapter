package com.turningpoint.chapterorganizer.security.service;

import com.turningpoint.chapterorganizer.entity.*;
import com.turningpoint.chapterorganizer.repository.UserRoleRepository;
import com.turningpoint.chapterorganizer.security.context.SecurityContext;
import com.turningpoint.chapterorganizer.security.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Main security service for RBAC operations and permission checking.
 */
@Service
public class SecurityService {
    
    private final UserRoleRepository userRoleRepository;
    
    @Autowired
    public SecurityService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }
    
    /**
     * Initialize security context for a user
     */
    public void initializeSecurityContext(Member user) {
        Set<UserRole> activeUserRoles = getUserActiveRoles(user.getId());
        SecurityContext context = new SecurityContext(user, activeUserRoles);
        SecurityContextHolder.setContext(context);
    }
    
    /**
     * Get all active roles for a user
     */
    public Set<UserRole> getUserActiveRoles(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findByUserIdAndIsActiveTrue(userId);
        return userRoles.stream()
                .filter(UserRole::isEffective)
                .collect(Collectors.toSet());
    }
    
    /**
     * Check if current user has permission
     */
    public boolean hasPermission(String resource, String action) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.hasPermission(resource, action);
    }
    
    /**
     * Check if current user has permission by name
     */
    public boolean hasPermission(String permissionName) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.hasPermission(permissionName);
    }
    
    /**
     * Check if current user has any of the specified permissions
     */
    public boolean hasAnyPermission(String... permissionNames) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.hasAnyPermission(permissionNames);
    }
    
    /**
     * Check if current user has all of the specified permissions
     */
    public boolean hasAllPermissions(String... permissionNames) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.hasAllPermissions(permissionNames);
    }
    
    /**
     * Check if current user has role
     */
    public boolean hasRole(String roleName) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.hasRole(roleName);
    }
    
    /**
     * Check if current user has any of the specified roles
     */
    public boolean hasAnyRole(String... roleNames) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.hasAnyRole(roleNames);
    }
    
    /**
     * Check if current user has all of the specified roles
     */
    public boolean hasAllRoles(String... roleNames) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.hasAllRoles(roleNames);
    }
    
    /**
     * Check if current user has minimum hierarchy level
     */
    public boolean hasMinimumHierarchyLevel(int minLevel) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.hasMinimumHierarchyLevel(minLevel);
    }
    
    /**
     * Check if current user has chapter-scoped permission
     */
    public boolean hasChapterPermission(Long chapterId, String resource, String action) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && 
               (context.hasGlobalPermission(resource, action) || 
                context.hasChapterPermission(chapterId, resource, action));
    }
    
    /**
     * Check if current user has chapter-scoped role
     */
    public boolean hasChapterRole(Long chapterId, String roleName) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && 
               (context.hasGlobalRole(roleName) || 
                context.hasChapterRole(chapterId, roleName));
    }
    
    /**
     * Check if current user is system administrator
     */
    public boolean isSystemAdmin() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.isSystemAdmin();
    }
    
    /**
     * Check if current user is chapter administrator
     */
    public boolean isChapterAdmin(Long chapterId) {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.isChapterAdmin(chapterId);
    }
    
    /**
     * Check if current user owns a resource
     */
    public boolean isResourceOwner(Long resourceOwnerId) {
        Member currentUser = SecurityContextHolder.getCurrentUser();
        return currentUser != null && currentUser.getId().equals(resourceOwnerId);
    }
    
    /**
     * Check if current user can access resource (ownership or admin rights)
     */
    public boolean canAccessResource(Long resourceOwnerId, Long chapterId) {
        return isResourceOwner(resourceOwnerId) || 
               isSystemAdmin() || 
               (chapterId != null && isChapterAdmin(chapterId));
    }
    
    /**
     * Validate permission with custom logic
     */
    public boolean validatePermission(String[] permissions, 
                                    boolean requireAll, 
                                    boolean chapterScoped, 
                                    Long chapterId) {
        if (permissions == null || permissions.length == 0) {
            return true; // No permissions required
        }
        
        if (requireAll) {
            for (String permission : permissions) {
                if (chapterScoped && chapterId != null) {
                    String[] parts = permission.split(":");
                    if (parts.length == 2 && !hasChapterPermission(chapterId, parts[0], parts[1])) {
                        return false;
                    }
                } else {
                    if (!hasPermission(permission)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            for (String permission : permissions) {
                if (chapterScoped && chapterId != null) {
                    String[] parts = permission.split(":");
                    if (parts.length == 2 && hasChapterPermission(chapterId, parts[0], parts[1])) {
                        return true;
                    }
                } else {
                    if (hasPermission(permission)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    /**
     * Validate role with custom logic
     */
    public boolean validateRole(String[] roles, 
                              boolean requireAll, 
                              boolean chapterScoped, 
                              Long chapterId, 
                              int minHierarchyLevel) {
        if (roles == null || roles.length == 0) {
            return hasMinimumHierarchyLevel(minHierarchyLevel);
        }
        
        if (requireAll) {
            for (String role : roles) {
                if (chapterScoped && chapterId != null) {
                    if (!hasChapterRole(chapterId, role)) {
                        return false;
                    }
                } else {
                    if (!hasRole(role)) {
                        return false;
                    }
                }
            }
            return hasMinimumHierarchyLevel(minHierarchyLevel);
        } else {
            for (String role : roles) {
                if (chapterScoped && chapterId != null) {
                    if (hasChapterRole(chapterId, role)) {
                        return hasMinimumHierarchyLevel(minHierarchyLevel);
                    }
                } else {
                    if (hasRole(role)) {
                        return hasMinimumHierarchyLevel(minHierarchyLevel);
                    }
                }
            }
            return false;
        }
    }
    
    /**
     * Get current user's security information summary
     */
    public SecuritySummary getCurrentUserSecuritySummary() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        
        return new SecuritySummary(
                context.getCurrentUser().getId(),
                context.getCurrentUser().getEmail(),
                context.getRoleNames(),
                context.getPermissionNames(),
                context.getHighestHierarchyLevel(),
                context.isSystemAdmin()
        );
    }
    
    /**
     * Security summary DTO
     */
    public static class SecuritySummary {
        private final Long userId;
        private final String userEmail;
        private final Set<String> roles;
        private final Set<String> permissions;
        private final int highestHierarchyLevel;
        private final boolean isSystemAdmin;
        
        public SecuritySummary(Long userId, String userEmail, Set<String> roles, 
                             Set<String> permissions, int highestHierarchyLevel, 
                             boolean isSystemAdmin) {
            this.userId = userId;
            this.userEmail = userEmail;
            this.roles = roles;
            this.permissions = permissions;
            this.highestHierarchyLevel = highestHierarchyLevel;
            this.isSystemAdmin = isSystemAdmin;
        }
        
        // Getters
        public Long getUserId() { return userId; }
        public String getUserEmail() { return userEmail; }
        public Set<String> getRoles() { return roles; }
        public Set<String> getPermissions() { return permissions; }
        public int getHighestHierarchyLevel() { return highestHierarchyLevel; }
        public boolean isSystemAdmin() { return isSystemAdmin; }
    }
}