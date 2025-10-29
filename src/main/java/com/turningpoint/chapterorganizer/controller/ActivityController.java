package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Activity;
import com.turningpoint.chapterorganizer.entity.ActivityType;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.service.ActivityService;
import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {
    
    private final ActivityService activityService;
    private final MemberService memberService;
    private final ChapterService chapterService;
    
    @Autowired
    public ActivityController(ActivityService activityService, MemberService memberService, ChapterService chapterService) {
        this.activityService = activityService;
        this.memberService = memberService;
        this.chapterService = chapterService;
    }
    
    // Get recent activities for dashboard feed
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentActivities(@RequestParam(defaultValue = "20") int limit) {
        try {
            List<Activity> activities = activityService.getRecentActivities(limit);
            
            // Transform to include additional display information
            List<Map<String, Object>> activityData = activities.stream()
                .map(this::transformActivityForDisplay)
                .collect(java.util.stream.Collectors.toList());
                
            return ResponseEntity.ok(activityData);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to fetch recent activities: " + e.getMessage()));
        }
    }
    
    // Get paginated activity feed
    @GetMapping("/feed")
    public ResponseEntity<?> getActivityFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Activity> activitiesPage = activityService.getActivityFeed(page, size);
            
            List<Map<String, Object>> activities = activitiesPage.getContent().stream()
                .map(this::transformActivityForDisplay)
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("activities", activities);
            response.put("totalElements", activitiesPage.getTotalElements());
            response.put("totalPages", activitiesPage.getTotalPages());
            response.put("currentPage", page);
            response.put("size", size);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to fetch activity feed: " + e.getMessage()));
        }
    }
    
    // Get activities by chapter
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<?> getChapterActivities(
            @PathVariable Long chapterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Optional<Chapter> chapterOpt = chapterService.getChapterById(chapterId);
            if (chapterOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Chapter not found"));
            }
            
            Page<Activity> activitiesPage = activityService.getChapterActivities(chapterOpt.get(), page, size);
            
            List<Map<String, Object>> activities = activitiesPage.getContent().stream()
                .map(this::transformActivityForDisplay)
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("activities", activities);
            response.put("totalElements", activitiesPage.getTotalElements());
            response.put("totalPages", activitiesPage.getTotalPages());
            response.put("currentPage", page);
            response.put("size", size);
            response.put("chapter", Map.of(
                "id", chapterOpt.get().getId(),
                "name", chapterOpt.get().getName()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to fetch chapter activities: " + e.getMessage()));
        }
    }
    
    // Get current user's activities
    @GetMapping("/my-activities")
    public ResponseEntity<?> getUserActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Optional<Member> member = memberService.getMemberByUsername(username);
            if (member.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User not found"));
            }
            
            Page<Activity> activitiesPage = activityService.getUserActivities(member.get(), page, size);
            
            List<Map<String, Object>> activities = activitiesPage.getContent().stream()
                .map(this::transformActivityForDisplay)
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("activities", activities);
            response.put("totalElements", activitiesPage.getTotalElements());
            response.put("totalPages", activitiesPage.getTotalPages());
            response.put("currentPage", page);
            response.put("size", size);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to fetch user activities: " + e.getMessage()));
        }
    }
    
    // Get activity analytics
    @GetMapping("/analytics/types")
    public ResponseEntity<?> getActivityTypeAnalytics(@RequestParam(defaultValue = "30") int days) {
        try {
            List<Object[]> typeCounts = activityService.getActivityCountsByType(days);
            
            List<Map<String, Object>> analytics = typeCounts.stream()
                .map(result -> {
                    ActivityType type = (ActivityType) result[0];
                    Long count = (Long) result[1];
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", type.name());
                    map.put("displayName", type.getDisplayName());
                    map.put("emoji", type.getEmoji());
                    map.put("count", count);
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
                
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to fetch activity analytics: " + e.getMessage()));
        }
    }
    
    // Get daily activity counts
    @GetMapping("/analytics/daily")
    public ResponseEntity<?> getDailyActivityAnalytics() {
        try {
            List<Object[]> dailyCounts = activityService.getDailyActivityCounts();
            
            List<Map<String, Object>> analytics = dailyCounts.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", result[0].toString());
                    map.put("count", ((Number) result[1]).intValue());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
                
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to fetch daily analytics: " + e.getMessage()));
        }
    }
    
    // Get most active chapters
    @GetMapping("/analytics/active-chapters")
    public ResponseEntity<?> getMostActiveChapters(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Object[]> activeChapters = activityService.getMostActiveChapters(days, limit);
            
            List<Map<String, Object>> analytics = activeChapters.stream()
                .map(result -> {
                    Chapter chapter = (Chapter) result[0];
                    Long count = (Long) result[1];
                    
                    Map<String, Object> chapterInfo = new HashMap<>();
                    chapterInfo.put("id", chapter.getId());
                    chapterInfo.put("name", chapter.getName());
                    chapterInfo.put("location", chapter.getLocation());
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("chapter", chapterInfo);
                    map.put("activityCount", count);
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
                
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to fetch active chapters: " + e.getMessage()));
        }
    }
    
    // Get critical activities
    @GetMapping("/critical")
    public ResponseEntity<?> getCriticalActivities() {
        try {
            List<Activity> criticalActivities = activityService.getRecentCriticalActivities();
            
            List<Map<String, Object>> activities = criticalActivities.stream()
                .map(this::transformActivityForDisplay)
                .collect(java.util.stream.Collectors.toList());
                
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to fetch critical activities: " + e.getMessage()));
        }
    }
    
    // Helper method to transform Activity entity for display
    private Map<String, Object> transformActivityForDisplay(Activity activity) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", activity.getId());
        result.put("type", activity.getType().name());
        result.put("displayName", activity.getType().getDisplayName());
        result.put("emoji", activity.getType().getEmoji());
        result.put("description", activity.getDescription());
        result.put("priority", activity.getPriority().name());
        result.put("priorityClass", activity.getPriority().getCssClass());
        result.put("createdAt", activity.getCreatedAt());
        result.put("entityType", activity.getEntityType());
        result.put("entityId", activity.getEntityId());
        result.put("metadata", activity.getMetadata());
        
        // Add user information if available
        if (activity.getUser() != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", activity.getUser().getId());
            userInfo.put("firstName", activity.getUser().getFirstName());
            userInfo.put("lastName", activity.getUser().getLastName());
            userInfo.put("username", activity.getUser().getUsername());
            result.put("user", userInfo);
        }
        
        // Add chapter information if available
        if (activity.getChapter() != null) {
            Map<String, Object> chapterInfo = new HashMap<>();
            chapterInfo.put("id", activity.getChapter().getId());
            chapterInfo.put("name", activity.getChapter().getName());
            chapterInfo.put("location", activity.getChapter().getLocation());
            result.put("chapter", chapterInfo);
        }
        
        return result;
    }
}