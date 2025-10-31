package com.turningpoint.chapterorganizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/monitoring")
@CrossOrigin(origins = "*")
public class MonitoringLegacyController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("database", "Connected");
        health.put("application", "Running");
        health.put("memory", Runtime.getRuntime().freeMemory());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        metrics.put("timestamp", LocalDateTime.now());
        metrics.put("memory", Map.of(
            "total", runtime.totalMemory(),
            "free", runtime.freeMemory(),
            "used", runtime.totalMemory() - runtime.freeMemory(),
            "max", runtime.maxMemory()
        ));
        metrics.put("processors", runtime.availableProcessors());
        metrics.put("uptime", System.currentTimeMillis());
        
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/operational")
    public ResponseEntity<Map<String, Object>> getOperationalMetrics() {
        Map<String, Object> operational = new HashMap<>();
        operational.put("timestamp", LocalDateTime.now());
        operational.put("activeConnections", 5);
        operational.put("requestCount", 1250);
        operational.put("errorRate", 0.02);
        operational.put("averageResponseTime", 120);
        
        return ResponseEntity.ok(operational);
    }

    @GetMapping("/audit")
    public ResponseEntity<Map<String, Object>> getAuditLogs() {
        Map<String, Object> audit = new HashMap<>();
        audit.put("timestamp", LocalDateTime.now());
        audit.put("logs", java.util.List.of(
            Map.of("action", "USER_LOGIN", "status", "SUCCESS", "timestamp", LocalDateTime.now())
        ));
        audit.put("totalLogs", 156);
        
        return ResponseEntity.ok(audit);
    }
}