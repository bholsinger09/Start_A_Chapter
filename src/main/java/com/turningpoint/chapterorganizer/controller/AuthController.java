package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.dto.RegistrationRequestDto;
import com.turningpoint.chapterorganizer.dto.RegistrationResponseDto;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.security.service.SecurityService;
import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.MemberService;
import com.turningpoint.chapterorganizer.util.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication controller for login/logout operations
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final MemberService memberService;
    private final SecurityService securityService;
    private final ChapterService chapterService;
    
    @Autowired
    public AuthController(MemberService memberService, SecurityService securityService, 
                         ChapterService chapterService) {
        this.memberService = memberService;
        this.securityService = securityService;
        this.chapterService = chapterService;
    }
    
    /**
     * Login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            // For now, let's create a simple test user authentication
            // In production, this would validate against stored credentials
            Member testUser = authenticateUser(request.getUsername(), request.getPassword());
            
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
     * Registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequestDto request) {
        try {
            // Validate chapter exists
            Optional<Chapter> chapterOptional = chapterService.getChapterById(request.getChapterId());
            if (chapterOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Invalid chapter ID")
                );
            }
            Chapter chapter = chapterOptional.get();

            // Check if username already exists
            if (memberService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Username already exists")
                );
            }

            // Check if email already exists
            if (memberService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Email already exists")
                );
            }

            // Create new member
            Member newMember = new Member();
            newMember.setFirstName(request.getFirstName());
            newMember.setLastName(request.getLastName());
            newMember.setEmail(request.getEmail());
            newMember.setUsername(request.getUsername());
            newMember.setPasswordHash(PasswordUtil.encode(request.getPassword()));
            newMember.setChapter(chapter);
            newMember.setPhoneNumber(request.getPhoneNumber());
            newMember.setMajor(request.getMajor());
            newMember.setGraduationYear(request.getGraduationYear());
            newMember.setActive(true);

            // Save the member
            Member savedMember = memberService.createMember(newMember);

            // Return success response
            RegistrationResponseDto response = new RegistrationResponseDto(
                "Registration successful",
                savedMember.getId(),
                savedMember.getUsername()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Registration failed: " + e.getMessage())
            );
        }
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
     * Authenticate user with username and password
     */
    private Member authenticateUser(String username, String password) {
        try {
            // Find member by username
            Optional<Member> memberOpt = memberService.getMemberByUsername(username);
            if (memberOpt.isEmpty()) {
                return null;
            }
            
            Member member = memberOpt.get();
            
            // Check if member has a password hash and verify it
            if (member.getPasswordHash() != null && 
                PasswordUtil.matches(password, member.getPasswordHash())) {
                return member;
            }
            
            // Fallback: Check for legacy test user
            if ("testuser".equals(username) && "password123".equals(password)) {
                // Find the first member to use as our authenticated test user
                return memberService.getAllMembers().stream()
                    .filter(m -> m.getEmail().contains("test") || m.getId() == 1L)
                    .findFirst()
                    .orElse(memberService.getAllMembers().get(0)); // Fallback to first member
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
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