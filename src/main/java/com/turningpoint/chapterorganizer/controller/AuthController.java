package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.security.service.SecurityService;
import com.turningpoint.chapterorganizer.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller for login/logout operations
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final MemberService memberService;
    private final SecurityService securityService;
    
    @Autowired
    public AuthController(MemberService memberService, SecurityService securityService) {
        this.memberService = memberService;
        this.securityService = securityService;
    }
    
    /**
     * Login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            // For now, let's create a simple test user authentication
            // In production, this would validate against stored credentials
            Member testUser = authenticateTestUser(request.getUsername(), request.getPassword());
            
            if (testUser != null) {
                // Initialize security context for the authenticated user
                securityService.initializeSecurityContext(testUser);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("user", Map.of(
                    "id", testUser.getId(),
                    "firstName", testUser.getFirstName(),
                    "lastName", testUser.getLastName(),
                    "email", testUser.getEmail(),
                    "role", testUser.getRole().toString()
                ));
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid credentials");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Authentication failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // Clear security context
        securityService.clearSecurityContext();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current user endpoint
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        try {
            Member currentUser = securityService.getCurrentUser();
            if (currentUser != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("user", Map.of(
                    "id", currentUser.getId(),
                    "firstName", currentUser.getFirstName(),
                    "lastName", currentUser.getLastName(),
                    "email", currentUser.getEmail(),
                    "role", currentUser.getRole().toString()
                ));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No authenticated user");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get current user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Simple test user authentication
     * In production, this would check against encrypted passwords in database
     */
    private Member authenticateTestUser(String username, String password) {
        // Simple test credentials
        if ("testuser".equals(username) && "password123".equals(password)) {
            // Find the first member to use as our authenticated test user
            // In production, you'd find by username or create a dedicated user table
            try {
                return memberService.getAllMembers().stream()
                    .filter(m -> m.getEmail().contains("test") || m.getId() == 1L)
                    .findFirst()
                    .orElse(memberService.getAllMembers().get(0)); // Fallback to first member
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * Login request DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;
        
        public LoginRequest() {}
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
}