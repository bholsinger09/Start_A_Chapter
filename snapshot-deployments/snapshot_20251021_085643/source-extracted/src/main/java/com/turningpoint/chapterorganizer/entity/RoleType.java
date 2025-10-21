package com.turningpoint.chapterorganizer.entity;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration of standard roles in the system with their hierarchy levels and permissions.
 */
public enum RoleType {
    
    // System Roles (Hierarchy Level 90-100)
    SUPER_ADMIN("Super Administrator", "Full system access with all permissions", 100, true,
        PermissionType.SYSTEM_ADMIN, PermissionType.SYSTEM_CONFIG, PermissionType.SYSTEM_MONITOR, 
        PermissionType.SYSTEM_AUDIT, PermissionType.USER_MANAGE, PermissionType.ROLE_MANAGE, 
        PermissionType.PERMISSION_MANAGE, PermissionType.CHAPTER_MANAGE, PermissionType.MEMBER_MANAGE,
        PermissionType.EVENT_MANAGE, PermissionType.FINANCE_MANAGE, PermissionType.REPORTS_CREATE,
        PermissionType.COMMUNICATION_MANAGE, PermissionType.CONTENT_MODERATE),
    
    SYSTEM_ADMIN("System Administrator", "System administration with limited user management", 95, true,
        PermissionType.SYSTEM_CONFIG, PermissionType.SYSTEM_MONITOR, PermissionType.SYSTEM_AUDIT,
        PermissionType.USER_READ, PermissionType.USER_UPDATE, PermissionType.ROLE_READ,
        PermissionType.CHAPTER_READ, PermissionType.REPORTS_VIEW, PermissionType.REPORTS_EXPORT),
    
    // Organizational Roles (Hierarchy Level 70-89)
    NATIONAL_DIRECTOR("National Director", "National organization oversight", 85, true,
        PermissionType.CHAPTER_MANAGE, PermissionType.MEMBER_READ, PermissionType.EVENT_READ,
        PermissionType.FINANCE_AUDIT, PermissionType.REPORTS_CREATE, PermissionType.COMMUNICATION_MANAGE),
    
    REGIONAL_DIRECTOR("Regional Director", "Regional chapter oversight", 80, true,
        PermissionType.CHAPTER_READ, PermissionType.CHAPTER_UPDATE, PermissionType.MEMBER_READ,
        PermissionType.EVENT_READ, PermissionType.REPORTS_VIEW, PermissionType.COMMUNICATION_SEND),
    
    // Chapter Leadership Roles (Hierarchy Level 50-69)
    CHAPTER_PRESIDENT("Chapter President", "Chapter leadership and full management", 65, false,
        PermissionType.CHAPTER_UPDATE, PermissionType.MEMBER_MANAGE, PermissionType.EVENT_MANAGE,
        PermissionType.FINANCE_MANAGE, PermissionType.ROLE_ASSIGN, PermissionType.REPORTS_CREATE,
        PermissionType.COMMUNICATION_MANAGE, PermissionType.CONTENT_MODERATE),
    
    CHAPTER_VICE_PRESIDENT("Chapter Vice President", "Chapter co-leadership", 60, false,
        PermissionType.CHAPTER_UPDATE, PermissionType.MEMBER_READ, PermissionType.MEMBER_UPDATE,
        PermissionType.EVENT_MANAGE, PermissionType.FINANCE_READ, PermissionType.REPORTS_VIEW,
        PermissionType.COMMUNICATION_SEND, PermissionType.CONTENT_CREATE),
    
    CHAPTER_SECRETARY("Chapter Secretary", "Chapter documentation and member management", 55, false,
        PermissionType.CHAPTER_READ, PermissionType.MEMBER_READ, PermissionType.MEMBER_UPDATE,
        PermissionType.EVENT_READ, PermissionType.EVENT_UPDATE, PermissionType.REPORTS_VIEW,
        PermissionType.COMMUNICATION_SEND, PermissionType.CONTENT_CREATE),
    
    CHAPTER_TREASURER("Chapter Treasurer", "Chapter financial management", 55, false,
        PermissionType.CHAPTER_READ, PermissionType.MEMBER_READ, PermissionType.EVENT_READ,
        PermissionType.FINANCE_MANAGE, PermissionType.REPORTS_VIEW, PermissionType.REPORTS_EXPORT),
    
    // Chapter Management Roles (Hierarchy Level 30-49)
    EVENT_COORDINATOR("Event Coordinator", "Event planning and management", 45, false,
        PermissionType.EVENT_MANAGE, PermissionType.MEMBER_READ, PermissionType.COMMUNICATION_SEND,
        PermissionType.CONTENT_CREATE, PermissionType.REPORTS_VIEW),
    
    MEMBERSHIP_COORDINATOR("Membership Coordinator", "Member recruitment and management", 45, false,
        PermissionType.MEMBER_MANAGE, PermissionType.MEMBER_APPROVE, PermissionType.EVENT_READ,
        PermissionType.COMMUNICATION_SEND, PermissionType.REPORTS_VIEW),
    
    COMMUNICATIONS_COORDINATOR("Communications Coordinator", "Chapter communications", 40, false,
        PermissionType.COMMUNICATION_MANAGE, PermissionType.CONTENT_MODERATE, PermissionType.EVENT_READ,
        PermissionType.MEMBER_READ, PermissionType.REPORTS_VIEW),
    
    CHAPTER_ADVISOR("Chapter Advisor", "Chapter guidance and mentorship", 50, false,
        PermissionType.CHAPTER_READ, PermissionType.MEMBER_READ, PermissionType.EVENT_READ,
        PermissionType.FINANCE_READ, PermissionType.REPORTS_VIEW, PermissionType.MEMBER_APPROVE),
    
    // Member Roles (Hierarchy Level 10-29)
    CHAPTER_MODERATOR("Chapter Moderator", "Content and activity moderation", 25, false,
        PermissionType.CONTENT_MODERATE, PermissionType.EVENT_MODERATE, PermissionType.MEMBER_READ,
        PermissionType.COMMUNICATION_SEND),
    
    ACTIVE_MEMBER("Active Member", "Full chapter member with event participation", 20, false,
        PermissionType.EVENT_RSVP, PermissionType.EVENT_READ, PermissionType.MEMBER_READ,
        PermissionType.CONTENT_CREATE, PermissionType.CONTENT_READ),
    
    ASSOCIATE_MEMBER("Associate Member", "Limited chapter membership", 15, false,
        PermissionType.EVENT_READ, PermissionType.EVENT_RSVP, PermissionType.MEMBER_READ,
        PermissionType.CONTENT_READ),
    
    // Guest/Visitor Roles (Hierarchy Level 1-9)
    GUEST("Guest", "Limited access for event attendance", 5, false,
        PermissionType.EVENT_READ, PermissionType.CONTENT_READ),
    
    PENDING_MEMBER("Pending Member", "Awaiting membership approval", 1, false,
        PermissionType.EVENT_READ, PermissionType.CONTENT_READ);
    
    private final String displayName;
    private final String description;
    private final int hierarchyLevel;
    private final boolean isSystemRole;
    private final Set<PermissionType> permissions;
    
    RoleType(String displayName, String description, int hierarchyLevel, boolean isSystemRole, 
             PermissionType... permissions) {
        this.displayName = displayName;
        this.description = description;
        this.hierarchyLevel = hierarchyLevel;
        this.isSystemRole = isSystemRole;
        this.permissions = Arrays.stream(permissions).collect(Collectors.toSet());
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }
    
    public boolean isSystemRole() {
        return isSystemRole;
    }
    
    public Set<PermissionType> getPermissions() {
        return permissions;
    }
    
    /**
     * Creates a Role entity from this enum value
     */
    public Role toRole() {
        return new Role(displayName, description, hierarchyLevel, isSystemRole);
    }
    
    /**
     * Finds a RoleType by its display name
     */
    public static RoleType fromDisplayName(String displayName) {
        for (RoleType type : values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No RoleType found for: " + displayName);
    }
    
    /**
     * Gets all system roles
     */
    public static Set<RoleType> getSystemRoles() {
        return Arrays.stream(values())
                .filter(RoleType::isSystemRole)
                .collect(Collectors.toSet());
    }
    
    /**
     * Gets all chapter roles (non-system roles)
     */
    public static Set<RoleType> getChapterRoles() {
        return Arrays.stream(values())
                .filter(role -> !role.isSystemRole())
                .collect(Collectors.toSet());
    }
    
    /**
     * Gets roles by hierarchy level range
     */
    public static Set<RoleType> getRolesByHierarchyRange(int minLevel, int maxLevel) {
        return Arrays.stream(values())
                .filter(role -> role.getHierarchyLevel() >= minLevel && role.getHierarchyLevel() <= maxLevel)
                .collect(Collectors.toSet());
    }
    
    /**
     * Checks if this role has higher authority than another role
     */
    public boolean hasHigherAuthorityThan(RoleType otherRole) {
        return this.hierarchyLevel > otherRole.getHierarchyLevel();
    }
    
    /**
     * Checks if this role has a specific permission
     */
    public boolean hasPermission(PermissionType permission) {
        return permissions.contains(permission);
    }
}