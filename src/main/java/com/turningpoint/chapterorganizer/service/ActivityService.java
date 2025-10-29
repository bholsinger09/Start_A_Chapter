package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.*;
import com.turningpoint.chapterorganizer.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    
    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }
    
    // Create new activity
    public Activity createActivity(ActivityType type, String description, Member user, Chapter chapter) {
        Activity activity = new Activity(type, description, user, chapter);
        return activityRepository.save(activity);
    }
    
    public Activity createActivity(ActivityType type, String description, String entityType, Long entityId, Member user, Chapter chapter) {
        Activity activity = new Activity(type, description, entityType, entityId, user, chapter);
        return activityRepository.save(activity);
    }
    
    public Activity createActivity(ActivityType type, String description, String entityType, Long entityId, Member user, Chapter chapter, ActivityPriority priority) {
        Activity activity = new Activity(type, description, entityType, entityId, user, chapter);
        activity.setPriority(priority);
        return activityRepository.save(activity);
    }
    
    // Get recent activities for feed
    @Transactional(readOnly = true)
    public List<Activity> getRecentActivities(int limit) {
        return activityRepository.findTop20ByOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .toList();
    }
    
    // Get paginated global activity feed
    @Transactional(readOnly = true)
    public Page<Activity> getActivityFeed(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return activityRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    // Get activities by chapter
    @Transactional(readOnly = true)
    public Page<Activity> getChapterActivities(Chapter chapter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return activityRepository.findByChapterOrderByCreatedAtDesc(chapter, pageable);
    }
    
    // Get activities by user
    @Transactional(readOnly = true)
    public Page<Activity> getUserActivities(Member user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return activityRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    // Get recent chapter activities (last 7 days)
    @Transactional(readOnly = true)
    public List<Activity> getRecentChapterActivities(Chapter chapter) {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        return activityRepository.findRecentActivitiesByChapter(chapter, since);
    }
    
    // Get activity analytics
    @Transactional(readOnly = true)
    public List<Object[]> getActivityCountsByType(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return activityRepository.getActivityCountsByType(since);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getDailyActivityCounts() {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        return activityRepository.getDailyActivityCounts(since);
    }
    
    // Get critical activities
    @Transactional(readOnly = true)
    public List<Activity> getRecentCriticalActivities() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return activityRepository.findRecentCriticalActivities(since);
    }
    
    // Helper methods for common activities
    public void logMemberJoined(Member member, Chapter chapter) {
        createActivity(
            ActivityType.MEMBER_JOINED,
            String.format("%s %s joined %s", member.getFirstName(), member.getLastName(), chapter.getName()),
            "Member",
            member.getId(),
            member,
            chapter,
            ActivityPriority.NORMAL
        );
    }
    
    public void logEventCreated(Event event, Member creator) {
        createActivity(
            ActivityType.EVENT_CREATED,
            String.format("New event '%s' scheduled for %s", event.getTitle(), event.getDate()),
            "Event",
            event.getId(),
            creator,
            event.getChapter(),
            ActivityPriority.NORMAL
        );
    }
    
    public void logBlogCreated(Blog blog, Member author) {
        createActivity(
            ActivityType.BLOG_CREATED,
            String.format("New blog post '%s' created by %s %s", 
                blog.getTitle(), 
                author.getFirstName(), 
                author.getLastName()),
            "Blog",
            blog.getId(),
            author,
            author.getChapter(),
            ActivityPriority.NORMAL
        );
    }
    
    public void logUserLogin(Member user) {
        createActivity(
            ActivityType.USER_LOGIN,
            String.format("%s %s logged in", user.getFirstName(), user.getLastName()),
            "Member",
            user.getId(),
            user,
            user.getChapter(),
            ActivityPriority.LOW
        );
    }
    
    public void logRoleChange(Member member, MemberRole oldRole, MemberRole newRole, Member changedBy) {
        createActivity(
            ActivityType.MEMBER_ROLE_CHANGED,
            String.format("%s %s role changed from %s to %s", 
                member.getFirstName(), 
                member.getLastName(), 
                oldRole, 
                newRole),
            "Member",
            member.getId(),
            changedBy,
            member.getChapter(),
            ActivityPriority.HIGH
        );
    }
    
    // Get activity by ID
    @Transactional(readOnly = true)
    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }
    
    // Get activities for specific entity
    @Transactional(readOnly = true)
    public List<Activity> getEntityActivities(String entityType, Long entityId) {
        return activityRepository.findByEntity(entityType, entityId);
    }
    
    // Get most active chapters
    @Transactional(readOnly = true)
    public List<Object[]> getMostActiveChapters(int days, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(0, limit);
        return activityRepository.getMostActiveChapters(since, pageable);
    }
    
    // Get most active users
    @Transactional(readOnly = true)
    public List<Object[]> getMostActiveUsers(int days, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(0, limit);
        return activityRepository.getMostActiveUsers(since, pageable);
    }
}