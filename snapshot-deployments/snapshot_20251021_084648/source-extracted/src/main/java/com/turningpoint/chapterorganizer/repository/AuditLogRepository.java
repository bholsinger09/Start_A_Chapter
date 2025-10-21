package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.AuditAction;
import com.turningpoint.chapterorganizer.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Find logs by user
    List<AuditLog> findByUserIdentifierOrderByTimestampDesc(String userIdentifier);

    // Find logs by entity type and ID
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);

    // Find logs by action type
    List<AuditLog> findByActionOrderByTimestampDesc(AuditAction action);

    // Find logs by chapter
    List<AuditLog> findByChapterIdOrderByTimestampDesc(Long chapterId);

    // Find logs within date range
    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<AuditLog> findByTimestampBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    // Find recent activity (paginated)
    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);

    // Find failed operations
    List<AuditLog> findBySuccessFalseOrderByTimestampDesc();

    // Count operations by user in time period
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userIdentifier = :userIdentifier AND a.timestamp >= :since")
    long countByUserSince(@Param("userIdentifier") String userIdentifier, @Param("since") LocalDateTime since);

    // Find login attempts
    @Query("SELECT a FROM AuditLog a WHERE a.action = 'LOGIN' AND a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<AuditLog> findRecentLogins(@Param("since") LocalDateTime since);

    // Find most active users
    @Query("SELECT a.userIdentifier, COUNT(a) as actionCount FROM AuditLog a " +
           "WHERE a.timestamp >= :since AND a.userIdentifier IS NOT NULL " +
           "GROUP BY a.userIdentifier ORDER BY actionCount DESC")
    List<Object[]> findMostActiveUsers(@Param("since") LocalDateTime since);

    // Find activity by chapter
    @Query("SELECT a FROM AuditLog a WHERE a.chapterId = :chapterId AND a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<AuditLog> findChapterActivity(@Param("chapterId") Long chapterId, @Param("since") LocalDateTime since);

    // Security: Find suspicious activities (multiple failed attempts from same IP)
    @Query("SELECT a.ipAddress, COUNT(a) as failureCount FROM AuditLog a " +
           "WHERE a.success = false AND a.timestamp >= :since AND a.ipAddress IS NOT NULL " +
           "GROUP BY a.ipAddress HAVING COUNT(a) > :threshold ORDER BY failureCount DESC")
    List<Object[]> findSuspiciousActivities(@Param("since") LocalDateTime since, @Param("threshold") long threshold);
}