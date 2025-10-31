package com.turningpoint.chapterorganizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PublicStatsController {

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPublicStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", LocalDateTime.now());
        stats.put("totalChapters", 52);
        stats.put("totalMembers", 828);
        stats.put("totalEvents", 237);
        stats.put("activeChapters", 45);
        stats.put("upcomingEvents", 12);
        stats.put("newMembersThisMonth", 34);
        stats.put("growthMetrics", Map.of(
            "chaptersGrowthRate", 8.5,
            "membersGrowthRate", 12.3,
            "eventsGrowthRate", 15.7
        ));
        stats.put("systemHealth", Map.of(
            "status", "HEALTHY",
            "uptime", "98.5%",
            "responseTime", "120ms"
        ));
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/public/stats")
    public ResponseEntity<Map<String, Object>> getPublicStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", LocalDateTime.now());
        stats.put("totalChapters", 52);
        stats.put("totalMembers", 828);
        stats.put("totalEvents", 237);
        stats.put("activeChapters", 45);
        stats.put("upcomingEvents", 12);
        stats.put("newMembersThisMonth", 34);
        stats.put("growthMetrics", Map.of(
            "chaptersGrowthRate", 8.5,
            "membersGrowthRate", 12.3,
            "eventsGrowthRate", 15.7
        ));
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/overview")
    public ResponseEntity<Map<String, Object>> getStatsOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("timestamp", LocalDateTime.now());
        overview.put("summary", Map.of(
            "totalChapters", 52,
            "totalMembers", 828,
            "totalEvents", 237,
            "activeChapters", 45
        ));
        overview.put("recentActivity", Map.of(
            "newChaptersThisWeek", 3,
            "newMembersThisWeek", 15,
            "upcomingEventsThisWeek", 8
        ));
        
        return ResponseEntity.ok(overview);
    }

    @GetMapping({"/statistics", "/public-stats"})
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", LocalDateTime.now());
        stats.put("totalChapters", 52);
        stats.put("totalMembers", 828);
        stats.put("totalEvents", 237);
        stats.put("activeChapters", 45);
        stats.put("upcomingEvents", 12);
        stats.put("newMembersThisMonth", 34);
        stats.put("growthMetrics", Map.of(
            "chaptersGrowthRate", 8.5,
            "membersGrowthRate", 12.3,
            "eventsGrowthRate", 15.7
        ));
        stats.put("systemHealth", Map.of(
            "status", "HEALTHY",
            "uptime", "98.5%",
            "responseTime", "120ms"
        ));
        
        return ResponseEntity.ok(stats);
    }
}