package com.turningpoint.chapterorganizer.entity;

public enum EventType {
    MEETING("Chapter Meeting"),
    SOCIAL("Social Event"),
    FUNDRAISER("Fundraiser"),
    VOLUNTEER("Volunteer Activity"),
    EDUCATIONAL("Educational Event"),
    POLITICAL("Political Event"),
    NETWORKING("Networking Event"),
    RECRUITMENT("Recruitment Event"),
    OTHER("Other");

    private final String displayName;

    EventType(String displayName) {
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