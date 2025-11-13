package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
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
            RegistrationData data = extractRegistrationData(registrationData);
            ValidationResult validation = validateRegistrationData(data);
            if (!validation.isValid()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", validation.getErrorMessage()));
            }

            Member member = createMemberFromData(data);
            handleChapterAssignment(member, data);
            Member savedMember = memberService.createMember(member);
            
            return buildSuccessResponse(savedMember);
            
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return handleDataIntegrityViolation(e);
        } catch (Exception e) {
            return handleGenericError(e);
        }
    }

    /**
     * Login endpoint (simplified - checks if member exists by email)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        try {
            String identifier = loginData.get("email");
            if (identifier == null) {
                identifier = loginData.get("identifier"); // Backward compatibility
            }
            String password = loginData.get("password");

            if (identifier == null || identifier.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email or username is required"));
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }

            Optional<Member> member = memberService.getMemberByEmail(identifier.trim().toLowerCase());
            if (member.isEmpty()) {
                member = memberService.getMemberByUsername(identifier.trim().toLowerCase());
            }
            if (member.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Member not found"));
            }

            Member foundMember = member.get();
            
            // TODO: Implement proper password hashing for production
            if (!password.equals(foundMember.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid password"));
            }

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

    /**
     * Extracts registration data from request map into strongly-typed object.
     * Centralizes type casting to prevent ClassCastException in business logic.
     */
    private RegistrationData extractRegistrationData(Map<String, Object> registrationData) {
        RegistrationData data = new RegistrationData();
        data.firstName = (String) registrationData.get("firstName");
        data.lastName = (String) registrationData.get("lastName");
        data.email = (String) registrationData.get("email");
        data.password = (String) registrationData.get("password");
        data.role = (String) registrationData.get("role");
        data.chapterName = (String) registrationData.get("chapterName");
        data.universityName = (String) registrationData.get("universityName");
        data.state = (String) registrationData.get("state");
        data.city = (String) registrationData.get("city");
        data.zipCode = (String) registrationData.get("zipCode");
        return data;
    }

    /**
     * Validates registration data integrity and business rules.
     * Prevents duplicate email registration and enforces password policy.
     */
    private ValidationResult validateRegistrationData(RegistrationData data) {
        if (data.firstName == null || data.firstName.trim().isEmpty()) {
            return ValidationResult.invalid("First name is required");
        }
        if (data.lastName == null || data.lastName.trim().isEmpty()) {
            return ValidationResult.invalid("Last name is required");
        }
        if (data.email == null || data.email.trim().isEmpty()) {
            return ValidationResult.invalid("Email is required");
        }
        if (data.password == null || data.password.trim().isEmpty()) {
            return ValidationResult.invalid("Password is required");
        }
        if (!isValidEmail(data.email)) {
            return ValidationResult.invalid("Invalid email format");
        }
        if (memberService.getMemberByEmail(data.email).isPresent()) {
            return ValidationResult.invalid("User with this email already exists");
        }
        if (data.password.length() < 6) {
            return ValidationResult.invalid("Password must be at least 6 characters long");
        }
        return ValidationResult.valid();
    }

    /**
     * Creates Member entity from validated registration data.
     * Applies data normalization (trim, lowercase) for consistency.
     */
    private Member createMemberFromData(RegistrationData data) {
        Member member = new Member();
        member.setFirstName(data.firstName.trim());
        member.setLastName(data.lastName.trim());
        member.setEmail(data.email.toLowerCase().trim());
        member.setPassword(data.password); // MemberService handles encoding
        member.setActive(true);
        return member;
    }

    /**
     * Handles optional chapter assignment during registration.
     * Creates new chapters when member registers for non-existing chapter.
     */
    private void handleChapterAssignment(Member member, RegistrationData data) {
        if (shouldAssignToChapter(data)) {
            Chapter chapter = findOrCreateChapter(data);
            assignMemberToChapter(member, chapter);
        }
    }

    private boolean shouldAssignToChapter(RegistrationData data) {
        return data.chapterName != null && !data.chapterName.trim().isEmpty() &&
               data.universityName != null && !data.universityName.trim().isEmpty();
    }

    private Chapter findOrCreateChapter(RegistrationData data) {
        List<Chapter> existingChapters = chapterService.searchChaptersByUniversity(data.universityName.trim());
        if (!existingChapters.isEmpty()) {
            return existingChapters.get(0);
        }
        return createNewChapter(data);
    }

    private Chapter createNewChapter(RegistrationData data) {
        Chapter chapter = new Chapter();
        chapter.setName(data.chapterName.trim());
        chapter.setUniversityName(data.universityName.trim());
        chapter.setState(data.state != null ? data.state.trim() : "");
        chapter.setCity(data.city != null ? data.city.trim() : "");
        chapter.setActive(true);
        return chapterService.createChapter(chapter);
    }

    private void assignMemberToChapter(Member member, Chapter chapter) {
        member.setChapter(chapter);
    }

    private ResponseEntity<Map<String, Object>> buildSuccessResponse(Member savedMember) {
        Map<String, Object> memberData = createMemberDataMap(savedMember);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Registration successful");
        response.put("member", memberData);
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createMemberDataMap(Member member) {
        Map<String, Object> memberData = new HashMap<>();
        memberData.put("id", member.getId());
        memberData.put("firstName", member.getFirstName());
        memberData.put("lastName", member.getLastName());
        memberData.put("email", member.getEmail());
        memberData.put("role", member.getRole());
        memberData.put("chapterId", member.getChapter() != null ? member.getChapter().getId() : null);
        return memberData;
    }

    private ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException e) {
        String message;
        if (e.getMessage().contains("UK_MEMBER_EMAIL")) {
            message = "Email address is already registered";
        } else if (e.getMessage().contains("UK_CHAPTER_UNIVERSITY")) {
            message = "Chapter with this university already exists";
        } else {
            message = "Registration failed due to data constraint";
        }
        return ResponseEntity.badRequest()
            .body(Map.of("success", false, "message", message));
    }

    private ResponseEntity<Map<String, Object>> handleGenericError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("success", false, "message", "Registration failed"));
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    // Inner classes for Clean Code refactoring
    private static class RegistrationData {
        String firstName;
        String lastName;
        String email;
        String password;
        String role;
        String chapterName;
        String universityName;
        String state;
        String city;
        String zipCode;
    }

    private static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
