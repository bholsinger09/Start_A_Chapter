package com.turningpoint.chapterorganizer.service.auth;

import com.turningpoint.chapterorganizer.dto.auth.RegistrationRequest;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.exception.DuplicateEmailException;
import com.turningpoint.chapterorganizer.service.MemberService;
import com.turningpoint.chapterorganizer.service.validation.ValidationService;
import org.springframework.stereotype.Service;

/**
 * Focused service for user registration operations.
 * Single Responsibility: Handle user registration business logic.
 * Fixes: Large Class and God Object code smells.
 */
@Service
public class RegistrationService {

    private final MemberService memberService;
    private final ValidationService validationService;

    public RegistrationService(MemberService memberService, ValidationService validationService) {
        this.memberService = memberService;
        this.validationService = validationService;
    }

    /**
     * Register a new user with validation.
     * Extracted from large AuthController to follow SRP.
     */
    public Member registerUser(RegistrationRequest request) {
        validateRegistrationRequest(request);
        checkUserDoesNotExist(request.getEmail());
        
        Member member = createMemberFromRequest(request);
        return memberService.createMember(member);
    }

    private void validateRegistrationRequest(RegistrationRequest request) {
        ValidationService.ValidationResult result = validationService.validateRegistration(request);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Registration validation failed: " + result.getErrorMessage());
        }
    }

    private void checkUserDoesNotExist(String email) {
        if (memberService.findMemberByEmail(email).isPresent()) {
            throw new DuplicateEmailException(email);
        }
    }

    private Member createMemberFromRequest(RegistrationRequest request) {
        Member member = new Member();
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPassword(request.getPassword()); // Should be hashed in production
        member.setPhoneNumber(request.getPhoneNumber());
        member.setMajor(request.getMajor());
        member.setGraduationYear(request.getGraduationYear());
        member.setActive(true);
        
        // Set username from email if not provided
        if (request.getEmail() != null) {
            member.setUsername(request.getEmail().split("@")[0]);
        }
        
        return member;
    }
}