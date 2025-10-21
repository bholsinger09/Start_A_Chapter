package com.turningpoint.chapterorganizer.dto;

import com.turningpoint.chapterorganizer.entity.Chapter;

public class RecommendationDto {
    private Chapter chapter;
    private String recommendationType;
    private double score;
    private String reason;
    private String description;

    // Constructors
    public RecommendationDto() {}

    public RecommendationDto(Chapter chapter, String recommendationType, double score, String reason, String description) {
        this.chapter = chapter;
        this.recommendationType = recommendationType;
        this.score = score;
        this.reason = reason;
        this.description = description;
    }

    // Getters and Setters
    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}