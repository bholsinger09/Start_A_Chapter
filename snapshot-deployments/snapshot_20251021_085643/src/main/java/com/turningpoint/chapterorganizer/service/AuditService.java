package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.AuditAction;
import com.turningpoint.chapterorganizer.entity.AuditLog;
import com.turningpoint.chapterorganizer.entity.OperationalMetric;
import com.turningpoint.chapterorganizer.repository.AuditLogRepository;
import com.turningpoint.chapterorganizer.repository.OperationalMetricRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final OperationalMetricRepository operationalMetricRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository, 
                       OperationalMetricRepository operationalMetricRepository,
                       ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.operationalMetricRepository = operationalMetricRepository;
        this.objectMapper = objectMapper;
    }

    // Audit Logging Methods

    public void logCreate(String entityType, Long entityId, Object newEntity, String userIdentifier) {
        try {
            String newValues = objectMapper.writeValueAsString(newEntity);
            AuditLog log = AuditLog.createAction(entityType, entityId, userIdentifier, newValues);
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }

    public void logUpdate(String entityType, Long entityId, Object oldEntity, Object newEntity, String userIdentifier) {
        try {
            String oldValues = objectMapper.writeValueAsString(oldEntity);
            String newValues = objectMapper.writeValueAsString(newEntity);
            AuditLog log = AuditLog.updateAction(entityType, entityId, userIdentifier, oldValues, newValues);
            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }

    public void logDelete(String entityType, Long entityId, Object deletedEntity, String userIdentifier) {
        try {
            String oldValues = objectMapper.writeValueAsString(deletedEntity);
            AuditLog log = AuditLog.deleteAction(entityType, entityId, userIdentifier, oldValues);
            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }

    public void logAction(AuditAction action, String entityType, Long entityId, String userIdentifier, 
                         String description, Long chapterId) {
        try {
            AuditLog log = new AuditLog(action, entityType, entityId, userIdentifier);
            log.setDescription(description);
            log.setChapterId(chapterId);
            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }

    public void logRSVP(Long eventId, Long memberId, String rsvpStatus, String userIdentifier, Long chapterId) {
        logAction(AuditAction.RSVP, "Event", eventId, userIdentifier, 
                 "RSVP status changed to: " + rsvpStatus + " for member: " + memberId, chapterId);
    }

    public void logAttendance(Long eventId, Long memberId, boolean attended, String userIdentifier, Long chapterId) {
        logAction(AuditAction.ATTENDANCE, "Event", eventId, userIdentifier, 
                 "Attendance marked as: " + (attended ? "Present" : "Absent") + " for member: " + memberId, chapterId);
    }

    public void logLogin(String userIdentifier, String ipAddress, String userAgent, boolean success) {
        try {
            AuditLog log = AuditLog.loginAction(userIdentifier, ipAddress, userAgent);
            log.setSuccess(success);
            if (!success) {
                log.setErrorMessage("Login failed");
            }
            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }

    // Query Methods

    public Page<AuditLog> getRecentActivity(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }

    public List<AuditLog> getUserActivity(String userIdentifier) {
        return auditLogRepository.findByUserIdentifierOrderByTimestampDesc(userIdentifier);
    }

    public List<AuditLog> getEntityHistory(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    public List<AuditLog> getChapterActivity(Long chapterId, LocalDateTime since) {
        return auditLogRepository.findChapterActivity(chapterId, since);
    }

    public List<AuditLog> getFailedOperations() {
        return auditLogRepository.findBySuccessFalseOrderByTimestampDesc();
    }

    public List<Object[]> getMostActiveUsers(LocalDateTime since) {
        return auditLogRepository.findMostActiveUsers(since);
    }

    // Metrics Methods

    public void recordMetric(String metricName, Double value, String unit, String category) {
        recordMetric(metricName, value, unit, category, null);
    }

    public void recordMetric(String metricName, Double value, String unit, String category, Long chapterId) {
        try {
            OperationalMetric metric = new OperationalMetric(metricName, value, unit, LocalDate.now(), category);
            metric.setChapterId(chapterId);
            operationalMetricRepository.save(metric);
        } catch (Exception e) {
            System.err.println("Failed to record metric: " + e.getMessage());
        }
    }

    public void recordDailyActiveUsers(long count) {
        recordMetric("daily_active_users", (double) count, "count", "usage");
    }

    public void recordApiResponseTime(double milliseconds) {
        recordMetric("avg_api_response_time", milliseconds, "milliseconds", "performance");
    }

    public void recordEventAttendanceRate(double rate, Long chapterId) {
        recordMetric("event_attendance_rate", rate, "percentage", "engagement", chapterId);
    }

    // Analytics Methods

    public Map<String, Object> getSystemHealthMetrics() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        
        try {
            return Map.of(
                "totalAuditLogs", auditLogRepository.count(),
                "totalMetrics", operationalMetricRepository.count(),
                "recentFailures", auditLogRepository.findBySuccessFalseOrderByTimestampDesc().size(),
                "weeklyActivity", auditLogRepository.findByTimestampBetween(
                    weekAgo.atStartOfDay(), today.atTime(23, 59, 59)).size()
            );
        } catch (Exception e) {
            return Map.of("error", "Failed to generate system health metrics");
        }
    }

    public List<OperationalMetric> getMetricsByCategory(String category) {
        return operationalMetricRepository.findByCategoryOrderByMetricDateDesc(category);
    }

    public List<OperationalMetric> getChapterMetrics(Long chapterId) {
        return operationalMetricRepository.findByChapterIdOrderByMetricDateDesc(chapterId);
    }

    public Double getAverageMetric(String metricName, LocalDate startDate, LocalDate endDate) {
        return operationalMetricRepository.calculateAverageByMetricNameAndDateRange(metricName, startDate, endDate);
    }

    // Security Methods

    public List<Object[]> getSuspiciousActivities(int hours, long failureThreshold) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditLogRepository.findSuspiciousActivities(since, failureThreshold);
    }

    public long getUserActivityCount(String userIdentifier, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditLogRepository.countByUserSince(userIdentifier, since);
    }
}