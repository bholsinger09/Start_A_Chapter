package com.turningpoint.chapterorganizer.entity;

public enum ActivityPriority {
    LOW("Low", "text-muted"),
    NORMAL("Normal", "text-primary"),  
    HIGH("High", "text-warning"),
    CRITICAL("Critical", "text-danger");
    
    private final String displayName;
    private final String cssClass;
    
    ActivityPriority(String displayName, String cssClass) {
        this.displayName = displayName;
        this.cssClass = cssClass;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCssClass() {
        return cssClass;
    }
}