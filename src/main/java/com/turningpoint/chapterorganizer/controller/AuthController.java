package com.turningpoint.chapterorganizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @GetMapping("/simple-test")
    public ResponseEntity<Map<String, Object>> simpleTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "AuthController working without dependencies");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/simple-test")
    public ResponseEntity<Map<String, Object>> loginViaSimpleTest(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login working via simple-test POST endpoint");
        response.put("username", request.get("username"));
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> handleAuth(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if this is a login request (has password but no registration fields)
        String action = (String) request.get("action");
        boolean isLogin = "login".equals(action) || 
                         (request.containsKey("password") && request.containsKey("username") && 
                          !request.containsKey("firstName") && !request.containsKey("stateOfResidence"));
        
        if (isLogin) {
            // Handle login
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("action", "login");
            response.put("username", request.get("username"));
        } else {
            // Handle registration
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("action", "register");
            response.put("username", request.get("username"));
        }
        
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login-test")
    public ResponseEntity<Map<String, Object>> loginTest(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login test endpoint working");
        response.put("username", request.get("username"));
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login endpoint working (basic test mode)");
        response.put("username", request.get("username"));
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
