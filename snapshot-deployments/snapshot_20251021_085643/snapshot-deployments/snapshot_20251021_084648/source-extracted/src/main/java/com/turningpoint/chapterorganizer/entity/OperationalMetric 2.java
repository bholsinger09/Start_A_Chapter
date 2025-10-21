package com.turningpoint.chapterorganizer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "operational_metrics")
public class OperationalMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Metric name is required")
    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @NotNull(message = "Metric value is required")
    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    @Column(name = "metric_unit")
    private String metricUnit; // e.g., "count", "percentage", "milliseconds"

    @NotNull(message = "Metric date is required")
    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "chapter_id")
    private Long chapterId; // For chapter-specific metrics

    @Column(name = "category")
    private String category; // e.g., "performance", "usage", "engagement"

    @Column(name = "description")
    private String description;

    @Column(name = "tags")
    private String tags; // JSON string for additional metadata

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public OperationalMetric() {
    }

    public OperationalMetric(String metricName, Double metricValue, LocalDate metricDate) {
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.metricDate = metricDate;
    }

    public OperationalMetric(String metricName, Double metricValue, String metricUnit, 
                           LocalDate metricDate, String category) {
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.metricUnit = metricUnit;
        this.metricDate = metricDate;
        this.category = category;
    }

    // Static factory methods for common metrics
    public static OperationalMetric dailyActiveUsers(Long count, LocalDate date) {
        return new OperationalMetric("daily_active_users", count.doubleValue(), "count", date, "usage");
    }

    public static OperationalMetric eventAttendanceRate(Double rate, LocalDate date, Long chapterId) {
        OperationalMetric metric = new OperationalMetric("event_attendance_rate", rate, "percentage", date, "engagement");
        metric.setChapterId(chapterId);
        return metric;
    }

    public static OperationalMetric apiResponseTime(Double milliseconds, LocalDate date) {
        return new OperationalMetric("avg_api_response_time", milliseconds, "milliseconds", date, "performance");
    }

    public static OperationalMetric newMembersCount(Long count, LocalDate date, Long chapterId) {
        OperationalMetric metric = new OperationalMetric("new_members_count", count.doubleValue(), "count", date, "growth");
        metric.setChapterId(chapterId);
        return metric;
    }

    public static OperationalMetric eventRSVPRate(Double rate, LocalDate date, Long chapterId) {
        OperationalMetric metric = new OperationalMetric("event_rsvp_rate", rate, "percentage", date, "engagement");
        metric.setChapterId(chapterId);
        return metric;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public String getMetricUnit() {
        return metricUnit;
    }

    public void setMetricUnit(String metricUnit) {
        this.metricUnit = metricUnit;
    }

    public LocalDate getMetricDate() {
        return metricDate;
    }

    public void setMetricDate(LocalDate metricDate) {
        this.metricDate = metricDate;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationalMetric that = (OperationalMetric) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OperationalMetric{" +
                "id=" + id +
                ", metricName='" + metricName + '\'' +
                ", metricValue=" + metricValue +
                ", metricDate=" + metricDate +
                ", category='" + category + '\'' +
                '}';
    }
}