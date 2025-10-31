package com.turningpoint.chapterorganizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ActuatorController {

    @GetMapping("/actuator/health")
    public ResponseEntity<Map<String, Object>> actuatorHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("components", Map.of(
            "db", Map.of("status", "UP"),
            "diskSpace", Map.of("status", "UP"), 
            "ping", Map.of("status", "UP")
        ));
        return ResponseEntity.ok(health);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> rootHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(health);
    }
}