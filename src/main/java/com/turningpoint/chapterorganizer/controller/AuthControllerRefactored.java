package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.dto.RegistrationRequest;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REFACTORED AuthController applying Robert Martin's Successive Refinement principle.
 * 
 * Before: Single 100+ line register method with multiple responsibilities
 * After: Multiple focused methods, each doing one thing well
 * 
 * NOTE: Temporarily disabled to avoid endpoint conflicts with original AuthController
 * To use this refactored version, comment out the original AuthController and
 * uncomment the annotations below.
 */
// @RestController
// @RequestMapping("/api/auth")
// @CrossOrigin(
//     origins = {"https://startachapter.duckdns.org", "http://startachapter.duckdns.org", "*"}, 
//     methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
//     allowedHeaders = "*",
//     allowCredentials = "false"
// )
public class AuthControllerRefactored {

    private final MemberService memberService;
    private final ChapterService chapterService;

    @Autowired
    public AuthControllerRefactored(MemberService memberService, ChapterService chapterService) {
        this.memberService = memberService;
        this.chapterService = chapterService;
    }

    /**
     * AFTER SUCCESSIVE REFINEMENT: Clean, focused method
     * Each step is extracted into a focused helper method
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> registrationData) {
        try {
            // Step 1: Parse and validate input (extracted method)
            RegistrationRequest request = parseRegistrationData(registrationData);
            
            // Step 2: Validate business rules (extracted method)
            ValidationResult validation = validateRegistrationRequest(request);
            if (!validation.isValid()) {
                return ResponseEntity.badRequest().body(Map.of("error", validation.getErrorMessage()));
            }
            
            // Step 3: Find or validate chapter (extracted method)
            Optional<Chapter> chapter = resolveChapter(request.getChapterId());
            if (request.getChapterId() != null && chapter.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid chapter selected"));
            }
            
            // Step 4: Create member entity (extracted method)
            Member newMember = createMemberFromRequest(request, chapter);
            
            // Step 5: Save member (delegates to service)
            Member createdMember = memberService.createMember(newMember);
            
            // Step 6: Build response (extracted method)
            Map<String, Object> response = buildSuccessResponse(createdMember);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Registration failed. Please try again."));
        }
    }

    /**
     * EXTRACTED METHOD 1: Parse registration data
     * Single responsibility: Convert Map to typed object
     */
    private RegistrationRequest parseRegistrationData(Map<String, Object> data) {
        RegistrationRequest request = new RegistrationRequest();
        
        request.setFirstName(extractString(data, "firstName"));
        request.setLastName(extractString(data, "lastName"));
        request.setEmail(extractString(data, "email"));
        request.setPhoneNumber(extractString(data, "phoneNumber"));
        request.setMajor(extractString(data, "major"));
        request.setPassword(extractString(data, "password"));
        
        // Handle username generation
        String username = extractString(data, "username");
        if (username == null || username.trim().isEmpty()) {
            String email = request.getEmail();
            username = (email != null && email.contains("@")) ? 
                      email.substring(0, email.indexOf('@')) : null;
        }
        request.setUsername(username);
        
        // Handle graduation year conversion
        request.setGraduationYear(convertToString(data.get("graduationYear")));
        
        // Handle chapter ID conversion  
        request.setChapterId(convertToLong(data.get("chapterId")));
        
        return request;
    }
    
    /**
     * EXTRACTED METHOD 2: Validate business rules
     * Single responsibility: Ensure data meets business requirements
     */
    private ValidationResult validateRegistrationRequest(RegistrationRequest request) {
        // Required field validations
        if (isEmpty(request.getFirstName())) {
            return ValidationResult.error("First name is required");
        }
        if (isEmpty(request.getLastName())) {
            return ValidationResult.error("Last name is required");
        }
        if (isEmpty(request.getEmail())) {
            return ValidationResult.error("Email is required");
        }
        if (isEmpty(request.getPassword())) {
            return ValidationResult.error("Password is required");
        }
        
        // Business rule validations
        if (request.getPassword().length() < 6) {
            return ValidationResult.error("Password must be at least 6 characters");
        }
        if (request.getUsername() != null && request.getUsername().length() < 3) {
            return ValidationResult.error("Username must be at least 3 characters");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * EXTRACTED METHOD 3: Resolve chapter
     * Single responsibility: Find and validate chapter
     */
    private Optional<Chapter> resolveChapter(Long chapterId) {
        if (chapterId == null) {
            return Optional.empty();
        }
        return chapterService.getChapterById(chapterId);
    }
    
    /**
     * EXTRACTED METHOD 4: Create member entity
     * Single responsibility: Build Member object from validated data
     */
    private Member createMemberFromRequest(RegistrationRequest request, Optional<Chapter> chapter) {
        Member member = new Member();
        
        // Set required fields with trimming
        member.setFirstName(request.getFirstName().trim());
        member.setLastName(request.getLastName().trim());
        member.setEmail(request.getEmail().trim().toLowerCase());
        member.setPassword(request.getPassword().trim());
        
        // Set optional fields with null-safe trimming
        member.setUsername(trimIfNotNull(request.getUsername()));
        member.setPhoneNumber(trimIfNotNull(request.getPhoneNumber()));
        member.setMajor(trimIfNotNull(request.getMajor()));
        member.setGraduationYear(trimIfNotNull(request.getGraduationYear()));
        
        // Set chapter if provided
        chapter.ifPresent(member::setChapter);
        
        return member;
    }
    
    /**
     * EXTRACTED METHOD 5: Build success response
     * Single responsibility: Create response data structure
     */
    private Map<String, Object> buildSuccessResponse(Member createdMember) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Registration successful");
        
        Map<String, Object> userData = createUserData(createdMember);
        response.put("user", userData);
        
        return response;
    }
    
    /**
     * EXTRACTED HELPER METHOD: Create user data
     * Single responsibility: Build user data portion of response
     */
    private Map<String, Object> createUserData(Member member) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", member.getId());
        userData.put("username", member.getEmail());
        userData.put("firstName", member.getFirstName());
        userData.put("lastName", member.getLastName());
        userData.put("email", member.getEmail());
        userData.put("action", "register");
        userData.put("loginTime", System.currentTimeMillis());
        
        // Add chapter info if available
        if (member.getChapter() != null) {
            userData.put("chapter", createChapterData(member.getChapter()));
        } else {
            userData.put("chapter", null);
        }
        
        return userData;
    }
    
    /**
     * EXTRACTED HELPER METHOD: Create chapter data
     * Single responsibility: Build chapter data portion of response
     */
    private Map<String, Object> createChapterData(Chapter chapter) {
        Map<String, Object> chapterData = new HashMap<>();
        chapterData.put("id", chapter.getId());
        chapterData.put("name", chapter.getName());
        chapterData.put("university", chapter.getUniversityName());
        return chapterData;
    }
    
    // ===============================
    // UTILITY METHODS (Private helpers)
    // ===============================
    
    private String extractString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return (value instanceof String) ? (String) value : null;
    }
    
    private String convertToString(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Number) {
            return obj.toString();
        }
        return null;
    }
    
    private Long convertToLong(Object obj) {
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        return null;
    }
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    private String trimIfNotNull(String str) {
        return (str != null) ? str.trim() : null;
    }
    
    // ===============================
    // INNER CLASSES (Support objects)
    // ===============================
    
    /**
     * Value object for validation results
     * Encapsulates validation state and error messaging
     */
    private static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }
}