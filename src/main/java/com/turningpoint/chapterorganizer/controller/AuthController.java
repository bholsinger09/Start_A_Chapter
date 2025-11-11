package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.service.MemberService;
import com.turningpoint.chapterorganizer.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {"https://startachapter.duckdns.org", "http://startachapter.duckdns.org", "*"}, 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "false"
)
public class AuthController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ChapterService chapterService;

    /**
     * Register new user (creates a new member)
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> registrationData) {
        try {
            // Extract registration data
            String firstName = (String) registrationData.get("firstName");
            String lastName = (String) registrationData.get("lastName");
            String email = (String) registrationData.get("email");
            String phoneNumber = (String) registrationData.get("phoneNumber");
            String major = (String) registrationData.get("major");
            String password = (String) registrationData.get("password");
            
            // Handle username - generate from email if not provided
            String username = (String) registrationData.get("username");
            if (username == null || username.trim().isEmpty()) {
                // Generate username from email part before @
                username = email.substring(0, email.indexOf('@'));
            } else {
                username = username.trim();
            }
            
            // Handle graduation year (could be String or Integer)
            String graduationYear = null;
            Object graduationYearObj = registrationData.get("graduationYear");
            if (graduationYearObj instanceof String) {
                graduationYear = (String) graduationYearObj;
            } else if (graduationYearObj instanceof Integer) {
                graduationYear = graduationYearObj.toString();
            } else if (graduationYearObj instanceof Number) {
                graduationYear = graduationYearObj.toString();
            }
            
            Long chapterId = null;
            
            // Handle chapter ID (could be String or Long)
            Object chapterIdObj = registrationData.get("chapterId");
            if (chapterIdObj instanceof String) {
                chapterId = Long.parseLong((String) chapterIdObj);
            } else if (chapterIdObj instanceof Number) {
                chapterId = ((Number) chapterIdObj).longValue();
            }

            // Validate required fields
            if (firstName == null || firstName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "First name is required"));
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Last name is required"));
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }
            if (password.length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters"));
            }
            if (username.length() < 3) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username must be at least 3 characters"));
            }

            // Get chapter (optional)
            Optional<Chapter> chapter = Optional.empty();
            if (chapterId != null) {
                chapter = chapterService.getChapterById(chapterId);
                if (chapter.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid chapter selected"));
                }
            }

            // Create new member
            Member newMember = new Member();
            newMember.setFirstName(firstName.trim());
            newMember.setLastName(lastName.trim());
            newMember.setEmail(email.trim().toLowerCase());
            newMember.setUsername(username.trim().toLowerCase());
            newMember.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
            newMember.setMajor(major != null ? major.trim() : null);
            newMember.setGraduationYear(graduationYear != null ? graduationYear.trim() : null);
            newMember.setPassword(password.trim()); // Store password (in production, this should be hashed)
            
            // Set chapter if selected (optional)
            if (chapter.isPresent()) {
                newMember.setChapter(chapter.get());
            }

            // Save member
            Member createdMember = memberService.createMember(newMember);

            // Return success response with user data (for frontend authentication)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", createdMember.getId());
            userData.put("username", createdMember.getEmail());
            userData.put("firstName", createdMember.getFirstName());
            userData.put("lastName", createdMember.getLastName());
            userData.put("email", createdMember.getEmail());
            userData.put("action", "register");
            userData.put("loginTime", System.currentTimeMillis());
            
            // Add chapter info if member has a chapter
            if (createdMember.getChapter() != null) {
                userData.put("chapter", Map.of(
                    "id", createdMember.getChapter().getId(),
                    "name", createdMember.getChapter().getName(),
                    "university", createdMember.getChapter().getUniversityName()
                ));
            } else {
                userData.put("chapter", null);
            }
            
            response.put("user", userData);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Login endpoint (simplified - checks if member exists by email)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        try {
            // Accept both "email" and "identifier" fields for backward compatibility
            String identifier = loginData.get("email");
            if (identifier == null) {
                identifier = loginData.get("identifier");
            }
            String password = loginData.get("password");

            if (identifier == null || identifier.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email or username is required"));
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }

            // Find member by email or username
            Optional<Member> member = memberService.getMemberByEmail(identifier.trim().toLowerCase());
            if (member.isEmpty()) {
                // Try finding by username if email lookup failed
                member = memberService.getMemberByUsername(identifier.trim().toLowerCase());
            }
            if (member.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Member not found"));
            }

            Member foundMember = member.get();
            
            // Validate password (in production, this should use password hashing)
            if (!password.equals(foundMember.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid password"));
            }

            // Return success response with user data
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", foundMember.getId());
            userData.put("username", foundMember.getEmail());
            userData.put("firstName", foundMember.getFirstName());
            userData.put("lastName", foundMember.getLastName());
            userData.put("email", foundMember.getEmail());
            userData.put("action", "login");
            userData.put("loginTime", System.currentTimeMillis());
            
            // Add chapter info if member has a chapter
            if (foundMember.getChapter() != null) {
                userData.put("chapter", Map.of(
                    "id", foundMember.getChapter().getId(),
                    "name", foundMember.getChapter().getName(),
                    "university", foundMember.getChapter().getUniversityName()
                ));
            } else {
                userData.put("chapter", null);
            }
            
            response.put("user", userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    /**
     * Get available chapters for registration
     */
    @GetMapping("/chapters")
    public ResponseEntity<List<Chapter>> getChapters() {
        try {
            List<Chapter> chapters = chapterService.getAllActiveChapters();
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
