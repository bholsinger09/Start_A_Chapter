package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ChapterService chapterService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        if (email == null || password == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Email and password are required");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Member> member = memberService.getMemberByEmail(email);

        if (member.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // For now, just check if member exists (password validation would go here)
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", member.get().getId());
        userData.put("email", member.get().getEmail());
        userData.put("firstName", member.get().getFirstName());
        userData.put("lastName", member.get().getLastName());
        userData.put("role", member.get().getRole());
        userData.put("chapterId", member.get().getChapter() != null ? member.get().getChapter().getId() : null);
        
        response.put("user", userData);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> registrationData) {
        try {
            String firstName = (String) registrationData.get("firstName");
            String lastName = (String) registrationData.get("lastName");
            String email = (String) registrationData.get("email");
            String roleStr = (String) registrationData.get("role");

            // Validate required fields
            if (firstName == null || lastName == null || email == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "First name, last name, and email are required");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if member already exists
            if (memberService.getMemberByEmail(email).isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Member with this email already exists");
                return ResponseEntity.badRequest().body(response);
            }

            // Get a default chapter (first available chapter)
            List<Chapter> chapters = chapterService.getAllChapters();
            if (chapters.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No chapters available for registration");
                return ResponseEntity.badRequest().body(response);
            }

            // Create new member
            Member member = new Member();
            member.setFirstName(firstName);
            member.setLastName(lastName);
            member.setEmail(email);
            member.setChapter(chapters.get(0)); // Assign to first available chapter
            
            // Set role
            MemberRole role = MemberRole.MEMBER;
            if (roleStr != null) {
                try {
                    role = MemberRole.valueOf(roleStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    role = MemberRole.MEMBER;
                }
            }
            member.setRole(role);

            // Save the member
            Member savedMember = memberService.createMember(member);

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", savedMember.getId());
            userData.put("email", savedMember.getEmail());
            userData.put("firstName", savedMember.getFirstName());
            userData.put("lastName", savedMember.getLastName());
            userData.put("role", savedMember.getRole());
            userData.put("chapterId", savedMember.getChapter().getId());
            
            response.put("user", userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
