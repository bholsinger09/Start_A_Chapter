package com.turningpoint.chapterorganizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("timestamp", LocalDateTime.now());
        overview.put("statistics", Map.of(
            "totalChapters", 52,
            "totalMembers", 828,
            "totalEvents", 237,
            "activeChapters", 45,
            "upcomingEvents", 12,
            "newMembersThisMonth", 34
        ));
        overview.put("recentActivity", Map.of(
            "lastLogin", LocalDateTime.now().minusMinutes(5),
            "lastEventCreated", LocalDateTime.now().minusHours(2),
            "lastMemberRegistered", LocalDateTime.now().minusHours(4)
        ));
        overview.put("systemStatus", Map.of(
            "health", "HEALTHY",
            "version", "1.0.0",
            "environment", "production",
            "lastDeployment", LocalDateTime.now().minusDays(1)
        ));
        
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/stats/public")
    public ResponseEntity<Map<String, Object>> getPublicStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", LocalDateTime.now());
        stats.put("totalChapters", 52);
        stats.put("totalMembers", 828);
        stats.put("totalEvents", 237);
        stats.put("activeChapters", 45);
        stats.put("growthMetrics", Map.of(
            "chaptersGrowthRate", 8.5,
            "membersGrowthRate", 12.3,
            "eventsGrowthRate", 15.7
        ));
        
        return ResponseEntity.ok(stats);
    }
}
