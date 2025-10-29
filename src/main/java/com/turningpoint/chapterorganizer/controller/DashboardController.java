package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.service.DashboardAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:3000", "https://startachapter.duckdns.org"})
public class DashboardController {

    @Autowired
    private DashboardAnalyticsService dashboardAnalyticsService;

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        try {
            Map<String, Object> statistics = dashboardAnalyticsService.getDashboardStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/chapters")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<Map<String, Object>>> getChapterStatistics() {
        try {
            List<Map<String, Object>> chapterStats = dashboardAnalyticsService.getChapterStatistics();
            return ResponseEntity.ok(chapterStats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/activity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('MEMBER')")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity(
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<Map<String, Object>> activity = dashboardAnalyticsService.getRecentActivity(limit);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/upcoming-events")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('MEMBER')")
    public ResponseEntity<List<Map<String, Object>>> getUpcomingEvents(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> events = dashboardAnalyticsService.getUpcomingEvents(limit);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user-data/{memberId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> getUserDashboardData(@PathVariable Long memberId) {
        try {
            Map<String, Object> userData = dashboardAnalyticsService.getUserDashboardData(memberId);
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/growth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Map<String, Object>> getMonthlyGrowthData() {
        try {
            Map<String, Object> growthData = dashboardAnalyticsService.getMonthlyGrowthData();
            return ResponseEntity.ok(growthData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        try {
            // Get basic statistics
            Map<String, Object> statistics = dashboardAnalyticsService.getDashboardStatistics();
            
            // Get recent activity (limited)
            List<Map<String, Object>> recentActivity = dashboardAnalyticsService.getRecentActivity(10);
            
            // Get upcoming events (limited)
            List<Map<String, Object>> upcomingEvents = dashboardAnalyticsService.getUpcomingEvents(5);
            
            // Combine into summary
            Map<String, Object> summary = Map.of(
                "statistics", statistics,
                "recentActivity", recentActivity,
                "upcomingEvents", upcomingEvents
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}