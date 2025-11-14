package com.turningpoint.chapterorganizer.controller.auth;

import com.turningpoint.chapterorganizer.dto.auth.LoginRequest;
import com.turningpoint.chapterorganizer.dto.auth.RegistrationRequest;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.exception.DuplicateEmailException;
import com.turningpoint.chapterorganizer.service.auth.AuthenticationService;
import com.turningpoint.chapterorganizer.service.auth.RegistrationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Refactored AuthController with focused responsibilities.
 * Fixes: Large Class, Long Methods, God Object code smells.
 * Uses composition and dependency injection for clean separation.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {"https://startachapter.duckdns.org", "http://startachapter.duckdns.org", "*"}, 
    methods = {RequestMethod.POST, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "false"
)
public class AuthRefactoredController {

    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;

    public AuthRefactoredController(RegistrationService registrationService, 
                                   AuthenticationService authenticationService) {
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
    }

    /**
     * Register new user - now focused and small.
     * Fixes: Long Method code smell.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegistrationRequest request) {
        try {
            Member member = registrationService.registerUser(request);
            return createSuccessResponse("Registration successful", member);
        } catch (DuplicateEmailException e) {
            return createErrorResponse(e.getUserFriendlyMessage(), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            return createErrorResponse("Registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Registration failed due to server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Login user - now focused and small.
     * Fixes: Long Method code smell.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            Optional<Member> member = authenticationService.authenticateUser(request);
            
            if (member.isPresent() && authenticationService.isUserActive(member.get())) {
                return createSuccessResponse("Login successful", member.get());
            } else {
                return createErrorResponse("Invalid credentials or inactive account", HttpStatus.UNAUTHORIZED);
            }
        } catch (IllegalArgumentException e) {
            return createErrorResponse("Login failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Login failed due to server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Extracted common response creation methods
    // Fixes: Duplicated Code smell
    private ResponseEntity<Map<String, Object>> createSuccessResponse(String message, Member member) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        response.put("member", createMemberResponse(member));
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    private Map<String, Object> createMemberResponse(Member member) {
        Map<String, Object> memberData = new HashMap<>();
        memberData.put("id", member.getId());
        memberData.put("firstName", member.getFirstName());
        memberData.put("lastName", member.getLastName());
        memberData.put("email", member.getEmail());
        memberData.put("role", member.getRole());
        memberData.put("chapterId", member.getChapter() != null ? member.getChapter().getId() : null);
        return memberData;
    }
}