package com.turningpoint.chapterorganizer.entity;

/**
 * Enumeration of standard permissions in the system.
 * These permissions follow a resource:action pattern.
 */
public enum PermissionType {
    
    // System Administration
    SYSTEM_ADMIN("system:admin", "system", "admin", "Full system administration access"),
    SYSTEM_CONFIG("system:config", "system", "config", "System configuration management"),
    SYSTEM_MONITOR("system:monitor", "system", "monitor", "System monitoring and health checks"),
    SYSTEM_AUDIT("system:audit", "system", "audit", "Access to system audit logs"),
    
    // User Management
    USER_CREATE("user:create", "user", "create", "Create new users"),
    USER_READ("user:read", "user", "read", "View user information"),
    USER_UPDATE("user:update", "user", "update", "Update user information"),
    USER_DELETE("user:delete", "user", "delete", "Delete users"),
    USER_MANAGE("user:manage", "user", "manage", "Full user management"),
    
    // Role Management
    ROLE_CREATE("role:create", "role", "create", "Create new roles"),
    ROLE_READ("role:read", "role", "read", "View role information"),
    ROLE_UPDATE("role:update", "role", "update", "Update role information"),
    ROLE_DELETE("role:delete", "role", "delete", "Delete roles"),
    ROLE_ASSIGN("role:assign", "role", "assign", "Assign roles to users"),
    ROLE_MANAGE("role:manage", "role", "manage", "Full role management"),
    
    // Permission Management
    PERMISSION_CREATE("permission:create", "permission", "create", "Create new permissions"),
    PERMISSION_READ("permission:read", "permission", "read", "View permission information"),
    PERMISSION_UPDATE("permission:update", "permission", "update", "Update permission information"),
    PERMISSION_DELETE("permission:delete", "permission", "delete", "Delete permissions"),
    PERMISSION_MANAGE("permission:manage", "permission", "manage", "Full permission management"),
    
    // Chapter Management
    CHAPTER_CREATE("chapter:create", "chapter", "create", "Create new chapters"),
    CHAPTER_READ("chapter:read", "chapter", "read", "View chapter information"),
    CHAPTER_UPDATE("chapter:update", "chapter", "update", "Update chapter information"),
    CHAPTER_DELETE("chapter:delete", "chapter", "delete", "Delete chapters"),
    CHAPTER_MANAGE("chapter:manage", "chapter", "manage", "Full chapter management"),
    CHAPTER_MODERATE("chapter:moderate", "chapter", "moderate", "Moderate chapter activities"),
    
    // Member Management
    MEMBER_CREATE("member:create", "member", "create", "Add new members"),
    MEMBER_READ("member:read", "member", "read", "View member information"),
    MEMBER_UPDATE("member:update", "member", "update", "Update member information"),
    MEMBER_DELETE("member:delete", "member", "delete", "Remove members"),
    MEMBER_MANAGE("member:manage", "member", "manage", "Full member management"),
    MEMBER_APPROVE("member:approve", "member", "approve", "Approve new member applications"),
    
    // Event Management
    EVENT_CREATE("event:create", "event", "create", "Create new events"),
    EVENT_READ("event:read", "event", "read", "View event information"),
    EVENT_UPDATE("event:update", "event", "update", "Update event information"),
    EVENT_DELETE("event:delete", "event", "delete", "Delete events"),
    EVENT_MANAGE("event:manage", "event", "manage", "Full event management"),
    EVENT_RSVP("event:rsvp", "event", "rsvp", "RSVP to events"),
    EVENT_MODERATE("event:moderate", "event", "moderate", "Moderate event activities"),
    
    // Finance Management
    FINANCE_READ("finance:read", "finance", "read", "View financial information"),
    FINANCE_MANAGE("finance:manage", "finance", "manage", "Manage chapter finances"),
    FINANCE_AUDIT("finance:audit", "finance", "audit", "Audit financial records"),
    
    // Reports and Analytics
    REPORTS_VIEW("reports:view", "reports", "view", "View reports and analytics"),
    REPORTS_CREATE("reports:create", "reports", "create", "Create custom reports"),
    REPORTS_EXPORT("reports:export", "reports", "export", "Export reports and data"),
    
    // Communication
    COMMUNICATION_SEND("communication:send", "communication", "send", "Send communications to members"),
    COMMUNICATION_MANAGE("communication:manage", "communication", "manage", "Manage communication settings"),
    
    // Content Management
    CONTENT_CREATE("content:create", "content", "create", "Create content"),
    CONTENT_READ("content:read", "content", "read", "View content"),
    CONTENT_UPDATE("content:update", "content", "update", "Update content"),
    CONTENT_DELETE("content:delete", "content", "delete", "Delete content"),
    CONTENT_PUBLISH("content:publish", "content", "publish", "Publish content"),
    CONTENT_MODERATE("content:moderate", "content", "moderate", "Moderate content");
    
    private final String permissionName;
    private final String resource;
    private final String action;
    private final String description;
    
    PermissionType(String permissionName, String resource, String action, String description) {
        this.permissionName = permissionName;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }
    
    public String getPermissionName() {
        return permissionName;
    }
    
    public String getResource() {
        return resource;
    }
    
    public String getAction() {
        return action;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Creates a Permission entity from this enum value
     */
    public Permission toPermission() {
        return new Permission(permissionName, resource, action, description, true);
    }
    
    /**
     * Finds a PermissionType by its permission name
     */
    public static PermissionType fromPermissionName(String permissionName) {
        for (PermissionType type : values()) {
            if (type.getPermissionName().equals(permissionName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No PermissionType found for: " + permissionName);
    }
    
    /**
     * Finds a PermissionType by resource and action
     */
    public static PermissionType fromResourceAction(String resource, String action) {
        for (PermissionType type : values()) {
            if (type.getResource().equals(resource) && type.getAction().equals(action)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No PermissionType found for resource: " + resource + ", action: " + action);
    }
    
    /**
     * Checks if a permission name exists in the enum
     */
    public static boolean exists(String permissionName) {
        for (PermissionType type : values()) {
            if (type.getPermissionName().equals(permissionName)) {
                return true;
            }
        }
        return false;
    }
}