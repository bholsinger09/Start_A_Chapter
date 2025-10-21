package com.turningpoint.chapterorganizer.entity;

public enum MemberRole {
    PRESIDENT("President"),
    VICE_PRESIDENT("Vice President"),
    SECRETARY("Secretary"),
    TREASURER("Treasurer"),
    OFFICER("Officer"),
    MEMBER("Member");

    private final String displayName;

    MemberRole(String displayName) {
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