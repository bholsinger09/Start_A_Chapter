package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.dto.RegistrationRequestDto;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import com.turningpoint.chapterorganizer.security.service.SecurityService;
import com.turningpoint.chapterorganizer.service.MemberService;
import com.turningpoint.chapterorganizer.util.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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
    private final ChapterRepository chapterRepository;
    
    @Autowired
    public AuthController(MemberService memberService, SecurityService securityService, ChapterRepository chapterRepository) {
        this.memberService = memberService;
        this.securityService = securityService;
        this.chapterRepository = chapterRepository;
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

            // Create new member - assign to first available chapter in state as temporary workaround
            Member newMember = new Member();
            newMember.setFirstName(request.getFirstName());
            newMember.setLastName(request.getLastName());
            newMember.setEmail(request.getEmail());
            newMember.setUsername(request.getUsername());
            newMember.setPasswordHash(PasswordUtil.encode(request.getPassword()));
            
            // Temporary workaround: assign to first chapter in state (database constraint requires chapter)
            String stateName = getStateName(request.getStateOfResidence());
            List<Chapter> chaptersInState = chapterRepository.findByStateIgnoreCaseAndActive(stateName, true);
            
            if (!chaptersInState.isEmpty()) {
                newMember.setChapter(chaptersInState.get(0)); // Assign to first chapter in state
            } else {
                // If no chapters in state, assign to first available chapter
                List<Chapter> allChapters = chapterRepository.findByActiveTrue();
                if (!allChapters.isEmpty()) {
                    newMember.setChapter(allChapters.get(0));
                } else {
                    return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "No chapters available for registration")
                    );
                }
            }
            
            newMember.setPhoneNumber(request.getPhoneNumber());
            newMember.setMajor(request.getMajor());
            newMember.setGraduationYear(request.getGraduationYear());
            newMember.setActive(true);

            // Save the member
            Member savedMember = memberService.createMember(newMember);

            // Return success response with state information for future chapter matching
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Registration successful! You can now find chapters in " + getStateName(request.getStateOfResidence()));
            responseData.put("userId", savedMember.getId());
            responseData.put("username", savedMember.getUsername());
            responseData.put("stateOfResidence", request.getStateOfResidence());

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Registration failed: " + e.getMessage())
            );
        }
    }

    /**
     * Helper method to convert state code to state name
     */
    private String getStateName(String stateCode) {
        Map<String, String> stateMap = Map.ofEntries(
            Map.entry("AL", "Alabama"), Map.entry("AK", "Alaska"), Map.entry("AZ", "Arizona"),
            Map.entry("AR", "Arkansas"), Map.entry("CA", "California"), Map.entry("CO", "Colorado"),
            Map.entry("CT", "Connecticut"), Map.entry("DE", "Delaware"), Map.entry("FL", "Florida"),
            Map.entry("GA", "Georgia"), Map.entry("HI", "Hawaii"), Map.entry("ID", "Idaho"),
            Map.entry("IL", "Illinois"), Map.entry("IN", "Indiana"), Map.entry("IA", "Iowa"),
            Map.entry("KS", "Kansas"), Map.entry("KY", "Kentucky"), Map.entry("LA", "Louisiana"),
            Map.entry("ME", "Maine"), Map.entry("MD", "Maryland"), Map.entry("MA", "Massachusetts"),
            Map.entry("MI", "Michigan"), Map.entry("MN", "Minnesota"), Map.entry("MS", "Mississippi"),
            Map.entry("MO", "Missouri"), Map.entry("MT", "Montana"), Map.entry("NE", "Nebraska"),
            Map.entry("NV", "Nevada"), Map.entry("NH", "New Hampshire"), Map.entry("NJ", "New Jersey"),
            Map.entry("NM", "New Mexico"), Map.entry("NY", "New York"), Map.entry("NC", "North Carolina"),
            Map.entry("ND", "North Dakota"), Map.entry("OH", "Ohio"), Map.entry("OK", "Oklahoma"),
            Map.entry("OR", "Oregon"), Map.entry("PA", "Pennsylvania"), Map.entry("RI", "Rhode Island"),
            Map.entry("SC", "South Carolina"), Map.entry("SD", "South Dakota"), Map.entry("TN", "Tennessee"),
            Map.entry("TX", "Texas"), Map.entry("UT", "Utah"), Map.entry("VT", "Vermont"),
            Map.entry("VA", "Virginia"), Map.entry("WA", "Washington"), Map.entry("WV", "West Virginia"),
            Map.entry("WI", "Wisconsin"), Map.entry("WY", "Wyoming"), Map.entry("DC", "District of Columbia")
        );
        return stateMap.getOrDefault(stateCode, stateCode);
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
     * Get active chapters by state for registration
     */
    @GetMapping("/chapters/active")
    public ResponseEntity<Map<String, Object>> getActiveChaptersByState(@RequestParam(required = false) String state) {
        try {
            List<Chapter> chapters;
            if (state != null && !state.trim().isEmpty()) {
                // Get chapters for specific state
                chapters = chapterRepository.findByStateIgnoreCaseAndActive(state.trim(), true);
            } else {
                // Get all active chapters
                chapters = chapterRepository.findByActiveTrue();
            }
            
            // Convert to simple DTOs to avoid serialization issues
            List<Map<String, Object>> chapterDtos = chapters.stream()
                .map(chapter -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", chapter.getId());
                    dto.put("name", chapter.getName());
                    dto.put("universityName", chapter.getUniversityName());
                    dto.put("city", chapter.getCity());
                    dto.put("state", chapter.getState());
                    dto.put("active", chapter.getActive());
                    return dto;
                })
                .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("chapters", chapterDtos);
            response.put("totalCount", chapters.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch chapters: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Simple test endpoint to check database connectivity
     */
    @GetMapping("/chapters/count")
    public ResponseEntity<Map<String, Object>> getChapterCount() {
        try {
            long count = chapterRepository.count();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalChapters", count);
            response.put("message", "Database connection successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Database error: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Initialize test chapters if database is empty
     */
    @PostMapping("/chapters/init")
    public ResponseEntity<Map<String, Object>> initializeTestChapters() {
        try {
            long count = chapterRepository.count();
            if (count > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Chapters already exist. Count: " + count);
                return ResponseEntity.ok(response);
            }

            // Create some test chapters
            Chapter californiaChapter = new Chapter();
            californiaChapter.setName("UC Berkeley Chapter");
            californiaChapter.setUniversityName("University of California, Berkeley");
            californiaChapter.setCity("Berkeley");
            californiaChapter.setState("California");
            californiaChapter.setActive(true);
            californiaChapter = chapterRepository.save(californiaChapter);

            Chapter texasChapter = new Chapter();
            texasChapter.setName("UT Austin Chapter");
            texasChapter.setUniversityName("University of Texas at Austin");
            texasChapter.setCity("Austin");
            texasChapter.setState("Texas");
            texasChapter.setActive(true);
            texasChapter = chapterRepository.save(texasChapter);

            Chapter newYorkChapter = new Chapter();
            newYorkChapter.setName("NYU Chapter");
            newYorkChapter.setUniversityName("New York University");
            newYorkChapter.setCity("New York");
            newYorkChapter.setState("New York");
            newYorkChapter.setActive(true);
            newYorkChapter = chapterRepository.save(newYorkChapter);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Created 3 test chapters");
            response.put("chapters", List.of(
                Map.of("id", californiaChapter.getId(), "name", californiaChapter.getName(), "state", californiaChapter.getState()),
                Map.of("id", texasChapter.getId(), "name", texasChapter.getName(), "state", texasChapter.getState()),
                Map.of("id", newYorkChapter.getId(), "name", newYorkChapter.getName(), "state", newYorkChapter.getState())
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to initialize chapters: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
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