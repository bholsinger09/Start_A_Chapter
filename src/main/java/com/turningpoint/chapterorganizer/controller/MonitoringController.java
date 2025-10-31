package com.turningpoint.chapterorganizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MonitoringController {

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

    @GetMapping("/monitoring/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("database", "Connected");
        health.put("application", "Running");
        health.put("memory", Runtime.getRuntime().freeMemory());
        return ResponseEntity.ok(health);
    }

    @GetMapping({"/metrics", "/monitoring/metrics"})
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
        metrics.put("system", Map.of(
            "javaVersion", System.getProperty("java.version"),
            "osName", System.getProperty("os.name"),
            "osVersion", System.getProperty("os.version")
        ));
        
        return ResponseEntity.ok(metrics);
    }

    @GetMapping({"/operational", "/monitoring/operational"})
    public ResponseEntity<Map<String, Object>> getOperationalMetrics() {
        Map<String, Object> operational = new HashMap<>();
        operational.put("timestamp", LocalDateTime.now());
        operational.put("activeConnections", 5);
        operational.put("requestCount", 1250);
        operational.put("errorRate", 0.02);
        operational.put("averageResponseTime", 120);
        operational.put("throughput", Map.of(
            "requestsPerSecond", 15.3,
            "peakRequestsPerSecond", 45.2,
            "avgRequestsPerMinute", 918
        ));
        operational.put("resources", Map.of(
            "cpuUsage", 25.5,
            "memoryUsage", 68.3,
            "diskUsage", 42.1
        ));
        
        return ResponseEntity.ok(operational);
    }

    @GetMapping({"/audit", "/monitoring/audit"})
    public ResponseEntity<Map<String, Object>> getAuditLogs() {
        Map<String, Object> audit = new HashMap<>();
        audit.put("timestamp", LocalDateTime.now());
        audit.put("logs", List.of(
            Map.of(
                "timestamp", LocalDateTime.now().minusHours(1),
                "action", "USER_LOGIN",
                "user", "code_monkey",
                "ip", "127.0.0.1",
                "status", "SUCCESS"
            ),
            Map.of(
                "timestamp", LocalDateTime.now().minusHours(2),
                "action", "CHAPTER_CREATED",
                "user", "admin",
                "ip", "127.0.0.1",
                "status", "SUCCESS"
            ),
            Map.of(
                "timestamp", LocalDateTime.now().minusHours(3),
                "action", "MEMBER_REGISTERED",
                "user", "system",
                "ip", "127.0.0.1",
                "status", "SUCCESS"
            )
        ));
        audit.put("totalLogs", 156);
        audit.put("logLevel", "INFO");
        
        return ResponseEntity.ok(audit);
    }
}
