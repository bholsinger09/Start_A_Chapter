package com.turningpoint.chapterorganizer.entity;

public enum AuditAction {
    CREATE("Create"),
    UPDATE("Update"),
    DELETE("Delete"),
    LOGIN("Login"),
    LOGOUT("Logout"),
    VIEW("View"),
    RSVP("RSVP"),
    ATTENDANCE("Attendance"),
    ACTIVATE("Activate"),
    DEACTIVATE("Deactivate"),
    EXPORT("Export"),
    IMPORT("Import"),
    SYSTEM("System Action");

    private final String displayName;

    AuditAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}