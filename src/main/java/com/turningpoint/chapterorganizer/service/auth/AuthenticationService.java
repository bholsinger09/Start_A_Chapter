package com.turningpoint.chapterorganizer.service.auth;

import com.turningpoint.chapterorganizer.dto.auth.LoginRequest;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Focused service for user authentication operations.
 * Single Responsibility: Handle user login and authentication logic.
 * Fixes: Large Class and God Object code smells.
 */
@Service
public class AuthenticationService {

    private final MemberService memberService;

    public AuthenticationService(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Authenticate user with email and password.
     * Extracted from large AuthController to follow SRP.
     */
    public Optional<Member> authenticateUser(LoginRequest request) {
        validateLoginRequest(request);
        
        Optional<Member> member = memberService.getMemberByEmail(request.getEmail());
        if (member.isPresent() && isPasswordValid(member.get(), request.getPassword())) {
            return member;
        }
        
        return Optional.empty();
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request == null || !request.isValid()) {
            throw new IllegalArgumentException("Invalid login credentials provided");
        }
    }

    private boolean isPasswordValid(Member member, String providedPassword) {
        // In production, this should use proper password hashing comparison
        // For now, using simple comparison for compatibility
        return member.getPassword() != null && member.getPassword().equals(providedPassword);
    }

    /**
     * Check if a user is active and allowed to login.
     */
    public boolean isUserActive(Member member) {
        return member != null && member.getActive() != null && member.getActive();
    }
}