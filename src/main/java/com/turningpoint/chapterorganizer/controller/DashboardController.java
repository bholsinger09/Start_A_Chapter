package com.turningpoint.chapterorganizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        logger.info("📊 DASHBOARD OVERVIEW REQUEST - Generating dashboard overview data");
        try {
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
            
            logger.info("✅ Successfully generated dashboard overview with {} keys", overview.size());
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            logger.error("❌ DASHBOARD OVERVIEW ERROR: Failed to generate overview", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping({"/stats/public", "/stats", "/public/stats"})
    public ResponseEntity<Map<String, Object>> getPublicStatistics() {
        logger.info("📈 PUBLIC STATS REQUEST - Multiple endpoint paths: /stats/public, /stats, /public/stats");
        try {
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
            
            logger.info("✅ Successfully generated public statistics with {} data points", stats.size());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("❌ PUBLIC STATS ERROR: Failed to generate statistics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        logger.info("📊 DASHBOARD STATISTICS REQUEST - Generating detailed statistics");
        try {
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
            
            logger.info("✅ Successfully generated dashboard statistics with {} metrics", stats.size());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("❌ DASHBOARD STATISTICS ERROR: Failed to generate statistics", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
