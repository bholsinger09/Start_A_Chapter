package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Activity;
import com.turningpoint.chapterorganizer.entity.ActivityType;
import com.turningpoint.chapterorganizer.entity.ActivityPriority;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    // Find recent activities (for activity feed)
    List<Activity> findTop20ByOrderByCreatedAtDesc();
    
    // Find activities by chapter
    Page<Activity> findByChapterOrderByCreatedAtDesc(Chapter chapter, Pageable pageable);
    
    // Find activities by user
    Page<Activity> findByUserOrderByCreatedAtDesc(Member user, Pageable pageable);
    
    // Find activities by type
    List<Activity> findByTypeOrderByCreatedAtDesc(ActivityType type);
    
    // Find activities by priority
    List<Activity> findByPriorityOrderByCreatedAtDesc(ActivityPriority priority);
    
    // Find activities within date range
    @Query("SELECT a FROM Activity a WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate ORDER BY a.createdAt DESC")
    List<Activity> findActivitiesInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find recent activities by chapter (last 7 days)
    @Query("SELECT a FROM Activity a WHERE a.chapter = :chapter AND a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<Activity> findRecentActivitiesByChapter(@Param("chapter") Chapter chapter, @Param("since") LocalDateTime since);
    
    // Get activity count by type for analytics
    @Query("SELECT a.type, COUNT(a) FROM Activity a WHERE a.createdAt >= :since GROUP BY a.type")
    List<Object[]> getActivityCountsByType(@Param("since") LocalDateTime since);
    
    // Get daily activity counts for the last 30 days
    @Query("SELECT DATE(a.createdAt) as activityDate, COUNT(a) as count FROM Activity a WHERE a.createdAt >= :since GROUP BY DATE(a.createdAt) ORDER BY activityDate DESC")
    List<Object[]> getDailyActivityCounts(@Param("since") LocalDateTime since);
    
    // Find activities by entity
    @Query("SELECT a FROM Activity a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.createdAt DESC")
    List<Activity> findByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);
    
    // Global activity feed with pagination
    Page<Activity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // Recent critical activities
    @Query("SELECT a FROM Activity a WHERE a.priority = 'CRITICAL' AND a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<Activity> findRecentCriticalActivities(@Param("since") LocalDateTime since);
    
    // Activity count for a specific chapter
    long countByChapter(Chapter chapter);
    
    // Activity count for a specific user
    long countByUser(Member user);
    
    // Most active chapters (by activity count)
    @Query("SELECT a.chapter, COUNT(a) as activityCount FROM Activity a WHERE a.createdAt >= :since GROUP BY a.chapter ORDER BY activityCount DESC")
    List<Object[]> getMostActiveChapters(@Param("since") LocalDateTime since, Pageable pageable);
    
    // Most active users (by activity count)
    @Query("SELECT a.user, COUNT(a) as activityCount FROM Activity a WHERE a.createdAt >= :since AND a.user IS NOT NULL GROUP BY a.user ORDER BY activityCount DESC")
    List<Object[]> getMostActiveUsers(@Param("since") LocalDateTime since, Pageable pageable);
}