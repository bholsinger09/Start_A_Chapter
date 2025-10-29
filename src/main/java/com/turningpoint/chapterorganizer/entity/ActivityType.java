package com.turningpoint.chapterorganizer.entity;

public enum ActivityType {
    // Member Activities
    MEMBER_JOINED("Member Joined", "👥"),
    MEMBER_UPDATED("Member Updated", "✏️"),
    MEMBER_ROLE_CHANGED("Role Changed", "👑"),
    MEMBER_ACTIVATED("Member Activated", "✅"),
    MEMBER_DEACTIVATED("Member Deactivated", "❌"),
    
    // Event Activities  
    EVENT_CREATED("Event Created", "📅"),
    EVENT_UPDATED("Event Updated", "📝"),
    EVENT_CANCELLED("Event Cancelled", "🚫"),
    EVENT_COMPLETED("Event Completed", "🎉"),
    
    // Blog Activities
    BLOG_CREATED("Blog Post Created", "📰"),
    BLOG_UPDATED("Blog Post Updated", "📝"),
    BLOG_PUBLISHED("Blog Post Published", "🚀"),
    BLOG_UNPUBLISHED("Blog Post Unpublished", "📄"),
    COMMENT_CREATED("Comment Added", "💬"),
    
    // Chapter Activities
    CHAPTER_CREATED("Chapter Created", "🏛️"),
    CHAPTER_UPDATED("Chapter Updated", "🔧"),
    
    // System Activities
    USER_LOGIN("User Login", "🔑"),
    USER_LOGOUT("User Logout", "🚪"),
    SYSTEM_BACKUP("System Backup", "💾"),
    
    // Administrative
    ADMIN_ACTION("Admin Action", "⚙️"),
    SECURITY_EVENT("Security Event", "🛡️");
    
    private final String displayName;
    private final String emoji;
    
    ActivityType(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getEmoji() {
        return emoji;
    }
    
    public String getFullDisplay() {
        return emoji + " " + displayName;
    }
}