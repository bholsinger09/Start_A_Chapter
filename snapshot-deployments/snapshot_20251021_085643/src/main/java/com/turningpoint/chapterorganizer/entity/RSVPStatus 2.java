package com.turningpoint.chapterorganizer.entity;

public enum RSVPStatus {
    PENDING("Pending Response"),
    ATTENDING("Will Attend"),
    NOT_ATTENDING("Will Not Attend"),
    MAYBE("Maybe Attending"),
    WAITLIST("On Waitlist");

    private final String displayName;

    RSVPStatus(String displayName) {
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