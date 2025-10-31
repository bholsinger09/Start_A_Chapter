package com.turningpoint.chapterorganizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class SystemStatusController {

    @GetMapping("/system/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("database", "Connected");
        response.put("application", "Running");
        response.put("memory", Runtime.getRuntime().freeMemory());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health-check")
    public ResponseEntity<Map<String, Object>> getHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("database", "Connected");
        response.put("application", "Running");
        response.put("memory", Runtime.getRuntime().freeMemory());
        return ResponseEntity.ok(response);
    }
}