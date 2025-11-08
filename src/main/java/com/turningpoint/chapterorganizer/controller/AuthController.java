package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Find member by username or email
            Optional<Member> memberOpt = memberService.findByUsernameOrEmail(
                loginRequest.getUsernameOrEmail(), 
                loginRequest.getUsernameOrEmail()
            );
            
            if (memberOpt.isEmpty()) {
                return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid username/email or password"));
            }
            
            Member member = memberOpt.get();
            
            // Simple password check (in production, use proper password hashing)
            if (!loginRequest.getPassword().equals(member.getPassword())) {
                return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid username/email or password"));
            }
            
            // Check if member is active
            if (!member.getActive()) {
                return ResponseEntity.status(403)
                    .body(Map.of("message", "Your account has been disabled"));
            }
            
            // Generate simple token (in production, use JWT)
            String token = generateToken(member);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", createUserResponse(member));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Login failed"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // Check if username already exists
            if (registerRequest.getUsername() != null && 
                memberService.findByUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.status(400)
                    .body(Map.of("message", "Username already exists", "field", "username"));
            }
            
            // Check if email already exists
            if (memberService.findByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.status(400)
                    .body(Map.of("message", "Email already exists", "field", "email"));
            }
            
            // Create new member
            Member member = new Member();
            member.setFirstName(registerRequest.getFirstName());
            member.setLastName(registerRequest.getLastName());
            member.setEmail(registerRequest.getEmail());
            member.setUsername(registerRequest.getUsername());
            member.setPassword(registerRequest.getPassword()); // In production, hash the password
            member.setPhoneNumber(registerRequest.getPhoneNumber());
            member.setMajor(registerRequest.getMajor());
            member.setGraduationYear(registerRequest.getGraduationYear() != null ? 
                registerRequest.getGraduationYear().toString() : null);
            member.setChapterId(registerRequest.getChapterId());
            
            // Convert string role to enum
            if (registerRequest.getRole() != null) {
                member.setRole(MemberRole.valueOf(registerRequest.getRole()));
            }
            member.setActive(true);
            
            Member savedMember = memberService.saveMember(member);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("user", createUserResponse(savedMember));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Registration failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In a real application, you would invalidate the token
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    private String generateToken(Member member) {
        // Simple token generation (in production, use JWT)
        return "token_" + member.getId() + "_" + System.currentTimeMillis();
    }

    private Map<String, Object> createUserResponse(Member member) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", member.getId());
        user.put("username", member.getUsername());
        user.put("email", member.getEmail());
        user.put("firstName", member.getFirstName());
        user.put("lastName", member.getLastName());
        user.put("role", member.getRole());
        user.put("chapterId", member.getChapterId());
        user.put("active", member.getActive());
        return user;
    }

    // Request DTOs
    public static class LoginRequest {
        private String usernameOrEmail;
        private String password;
        
        // Getters and setters
        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String username;
        private String password;
        private String phoneNumber;
        private String major;
        private Integer graduationYear;
        private Long chapterId;
        private String role;
        
        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getMajor() { return major; }
        public void setMajor(String major) { this.major = major; }
        public Integer getGraduationYear() { return graduationYear; }
        public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
        public Long getChapterId() { return chapterId; }
        public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}