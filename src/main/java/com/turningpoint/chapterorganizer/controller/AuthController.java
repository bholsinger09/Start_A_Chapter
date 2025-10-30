package com.turningpoint.chapterorganizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @GetMapping("/simple-test")
    public ResponseEntity<Map<String, Object>> simpleTestGet() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "AuthController working without dependencies");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/simple-test")
    public ResponseEntity<Map<String, Object>> simpleTestPost(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login working via simple-test POST endpoint");
        response.put("timestamp", System.currentTimeMillis());
        
        if (request != null && request.containsKey("username")) {
            response.put("username", request.get("username"));
        } else {
            response.put("username", null);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login endpoint working (basic test mode)");
        response.put("timestamp", System.currentTimeMillis());
        
        if (request != null && request.containsKey("username")) {
            response.put("username", request.get("username"));
        } else {
            response.put("username", null);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login-test")
    public ResponseEntity<Map<String, Object>> loginTest(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login test endpoint working");
        response.put("timestamp", System.currentTimeMillis());
        
        if (request != null && request.containsKey("username")) {
            response.put("username", request.get("username"));
        } else {
            response.put("username", null);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("timestamp", System.currentTimeMillis());
        
        // Simple logic to distinguish between login and registration
        if (request != null && request.containsKey("action")) {
            String action = (String) request.get("action");
            if ("login".equals(action)) {
                response.put("message", "Login successful");
                response.put("action", "login");
            } else {
                response.put("message", "Registration successful");
                response.put("action", "register");
            }
        } else if (request != null && 
                   (request.containsKey("firstName") || request.containsKey("stateOfResidence"))) {
            // Has registration fields
            response.put("message", "Registration successful");
            response.put("action", "register");
        } else if (request != null && 
                   request.containsKey("username") && request.containsKey("password")) {
            // Only has login fields
            response.put("message", "Login successful");
            response.put("action", "login");
        } else {
            // Empty or minimal request
            response.put("message", "Registration successful");
            response.put("action", "register");
        }
        
        if (request != null && request.containsKey("username")) {
            response.put("username", request.get("username"));
        } else {
            response.put("username", null);
        }
        
        return ResponseEntity.ok(response);
    }
}
