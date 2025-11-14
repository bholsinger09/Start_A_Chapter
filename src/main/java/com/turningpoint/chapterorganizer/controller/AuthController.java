package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.MemberService;
import com.turningpoint.chapterorganizer.exception.MemberRegistrationException;
import com.turningpoint.chapterorganizer.exception.MemberAuthenticationException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Profile("disabled")  // Disabled in favor of AuthRefactoredController
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
     * Implements try-catch-finally first approach with proper resource management
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> registrationData) {
        RegistrationData data = null;
        Member member = null;
        boolean registrationStarted = false;
        
        try {
            // Write try-catch-finally block first, then implement business logic
            registrationStarted = true;
            
            // Extract and validate data - fail fast with specific exceptions
            data = extractRegistrationData(registrationData);
            validateRegistrationDataOrThrow(data);
            
            // Happy path: create and save member
            member = createMemberFromData(data);
            handleChapterAssignment(member, data);
            Member savedMember = memberService.createMember(member);
            
            return buildSuccessResponse(savedMember);
            
        } catch (MemberRegistrationException ex) {
            // Specific registration failures are handled by GlobalExceptionHandler
            throw ex;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Transform database constraints into domain exceptions
            handleDataIntegrityConstraintViolation(ex, data);
            throw new MemberRegistrationException(MemberRegistrationException.RegistrationFailureReason.DATA_INTEGRITY_VIOLATION);
        } catch (Exception ex) {
            // Transform unexpected errors into domain exceptions
            throw new RuntimeException("Registration process failed due to system error", ex);
        } finally {
            // Cleanup and logging in finally block
            if (registrationStarted) {
                logRegistrationAttempt(data, member != null);
            }
        }
    }

    /**
     * Login endpoint with try-catch-finally first approach
     * Implements fail-fast authentication with specific exceptions
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String identifier = null;
        String password = null;
        boolean loginAttempted = false;
        
        try {
            // Write try-catch-finally block first
            loginAttempted = true;
            
            // Extract credentials - fail fast if missing
            identifier = extractIdentifier(loginData);
            password = extractPassword(loginData);
            validateCredentials(identifier, password);
            
            // Find member - fail fast if not found
            Member authenticatedMember = findAndAuthenticateMember(identifier, password);
            
            // Happy path: build successful login response
            return buildLoginSuccessResponse(authenticatedMember);
            
        } catch (MemberAuthenticationException ex) {
            // Specific authentication failures handled by GlobalExceptionHandler
            throw ex;
        } catch (Exception ex) {
            // Transform unexpected errors into domain exceptions
            throw new RuntimeException("Login process failed due to system error", ex);
        } finally {
            // Log authentication attempt in finally block
            if (loginAttempted) {
                logAuthenticationAttempt(identifier, false); // Will be overridden by success case
            }
        }
    }
    
    /**
     * Extracts identifier with backward compatibility support
     */
    private String extractIdentifier(Map<String, String> loginData) {
        String identifier = loginData.get("email");
        if (identifier == null) {
            identifier = loginData.get("identifier"); // Backward compatibility
        }
        return identifier;
    }
    
    /**
     * Extracts password from login data
     */
    private String extractPassword(Map<String, String> loginData) {
        return loginData.get("password");
    }
    
    /**
     * Validates credentials are present and properly formatted
     */
    private void validateCredentials(String identifier, String password) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw MemberAuthenticationException.missingCredentials();
        }
        if (password == null || password.trim().isEmpty()) {
            throw MemberAuthenticationException.missingCredentials();
        }
    }
    
    /**
     * Finds member by identifier and validates password
     * Returns authenticated member or throws specific exception
     */
    private Member findAndAuthenticateMember(String identifier, String password) {
        String normalizedIdentifier = identifier.trim().toLowerCase();
        
        // Try to find by email first, then by username
        Optional<Member> memberOpt = memberService.getMemberByEmail(normalizedIdentifier);
        if (memberOpt.isEmpty()) {
            memberOpt = memberService.getMemberByUsername(normalizedIdentifier);
        }
        
        if (memberOpt.isEmpty()) {
            throw MemberAuthenticationException.memberNotFound(identifier);
        }
        
        Member member = memberOpt.get();
        
        // Check if account is active
        if (member.getActive() != null && !member.getActive()) {
            throw MemberAuthenticationException.accountDisabled(identifier);
        }
        
        // Validate password (TODO: implement proper password hashing)
        if (!password.equals(member.getPassword())) {
            throw MemberAuthenticationException.invalidPassword();
        }
        
        return member;
    }
    
    /**
     * Builds successful login response with user data
     */
    private ResponseEntity<Map<String, Object>> buildLoginSuccessResponse(Member member) {
        logAuthenticationAttempt(member.getEmail(), true); // Override finally block logging
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("user", createUserDataMap(member));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Creates user data map without exposing sensitive information
     */
    private Map<String, Object> createUserDataMap(Member member) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", member.getId());
        userData.put("username", member.getEmail());
        userData.put("firstName", member.getFirstName());
        userData.put("lastName", member.getLastName());
        userData.put("email", member.getEmail());
        userData.put("action", "login");
        userData.put("loginTime", System.currentTimeMillis());
        
        // Add chapter information if member belongs to one
        if (member.getChapter() != null) {
            userData.put("chapter", Map.of(
                "id", member.getChapterId(),
                "name", member.getChapterName(),
                "university", member.getChapterUniversity()
            ));
        } else {
            userData.put("chapter", Map.of()); // Empty map instead of null
        }
        
        return userData;
    }
    
    /**
     * Logs authentication attempts for security monitoring
     */
    private void logAuthenticationAttempt(String identifier, boolean successful) {
        // In production, use proper logging framework with security considerations
        String status = successful ? "SUCCESS" : "FAILED";
        System.out.println("Authentication " + status + " for identifier: " + 
            (identifier != null ? identifier : "null") + " at " + System.currentTimeMillis());
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
        data.chapterName = (String) registrationData.get("chapterName");
        data.universityName = (String) registrationData.get("universityName");
        data.state = (String) registrationData.get("state");
        data.city = (String) registrationData.get("city");
        return data;
    }

    /**
     * Validates registration data and throws specific exceptions for failures.
     * Implements fail-fast validation with context-specific exceptions.
     */
    private void validateRegistrationDataOrThrow(RegistrationData data) {
        // Validate required fields first
        if (data.firstName == null || data.firstName.trim().isEmpty()) {
            throw MemberRegistrationException.missingRequiredField("First name");
        }
        if (data.lastName == null || data.lastName.trim().isEmpty()) {
            throw MemberRegistrationException.missingRequiredField("Last name");
        }
        if (data.email == null || data.email.trim().isEmpty()) {
            throw MemberRegistrationException.missingRequiredField("Email");
        }
        if (data.password == null || data.password.trim().isEmpty()) {
            throw MemberRegistrationException.missingRequiredField("Password");
        }
        
        // Validate email format
        if (!isValidEmail(data.email)) {
            throw MemberRegistrationException.invalidEmailFormat(data.email);
        }
        
        // Check for duplicate email
        if (memberService.getMemberByEmail(data.email).isPresent()) {
            throw MemberRegistrationException.duplicateEmail(data.email);
        }
        
        // Validate password strength
        if (data.password.length() < 6) {
            throw MemberRegistrationException.weakPassword();
        }
    }
    
    /**
     * Handles database constraint violations by transforming them into domain exceptions
     */
    private void handleDataIntegrityConstraintViolation(
            org.springframework.dao.DataIntegrityViolationException ex, RegistrationData data) {
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("UK_MEMBER_EMAIL")) {
                throw MemberRegistrationException.duplicateEmail(data.email);
            } else if (message.contains("UK_CHAPTER_UNIVERSITY")) {
                throw new MemberRegistrationException(
                    MemberRegistrationException.RegistrationFailureReason.DATA_INTEGRITY_VIOLATION,
                    "Chapter registration conflict at university"
                );
            }
        }
    }
    
    /**
     * Logs registration attempts for monitoring and debugging
     */
    private void logRegistrationAttempt(RegistrationData data, boolean successful) {
        // In production, use proper logging framework
        String status = successful ? "SUCCESS" : "FAILED";
        System.out.println("Registration " + status + " for email: " + 
            (data != null ? data.email : "null") + " at " + System.currentTimeMillis());
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
        memberData.put("chapterId", member.getChapterId());
        return memberData;
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
        String chapterName;
        String universityName;
        String state;
        String city;
    }
}
