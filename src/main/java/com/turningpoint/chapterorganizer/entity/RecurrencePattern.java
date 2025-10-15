package com.turningpoint.chapterorganizer.entity;

public enum RecurrencePattern {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"), 
    YEARLY("Yearly");

    private final String displayName;

    RecurrencePattern(String displayName) {
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