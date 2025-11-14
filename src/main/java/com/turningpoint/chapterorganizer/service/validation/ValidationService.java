package com.turningpoint.chapterorganizer.service.validation;

import com.turningpoint.chapterorganizer.dto.auth.LoginRequest;
import com.turningpoint.chapterorganizer.dto.auth.RegistrationRequest;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.util.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Focused service for validation operations.
 * Fixes: Long Methods, Duplicated Code, and Feature Envy code smells.
 * Single Responsibility: Centralize validation logic.
 */
@Service
public class ValidationService {

    /**
     * Validate registration request with detailed error messages.
     * Fixes: Long Method smell by extracting validation logic.
     */
    public ValidationResult validateRegistration(RegistrationRequest request) {
        ValidationResult result = new ValidationResult();

        if (request == null) {
            result.addError("Registration request cannot be null");
            return result;
        }

        validateName(request.getFirstName(), "First name", result);
        validateName(request.getLastName(), "Last name", result);
        validateEmail(request.getEmail(), result);
        validatePassword(request.getPassword(), result);
        validatePhoneNumber(request.getPhoneNumber(), result);

        return result;
    }

    /**
     * Validate login request.
     * Focused validation for authentication.
     */
    public ValidationResult validateLogin(LoginRequest request) {
        ValidationResult result = new ValidationResult();

        if (request == null) {
            result.addError("Login request cannot be null");
            return result;
        }

        if (ValidationUtils.isBlank(request.getEmail())) {
            result.addError("Email is required");
        } else if (!ValidationUtils.isValidEmail(request.getEmail())) {
            result.addError("Invalid email format");
        }

        if (ValidationUtils.isBlank(request.getPassword())) {
            result.addError("Password is required");
        }

        return result;
    }

    /**
     * Validate member entity for creation/updates.
     * Centralized member validation logic.
     */
    public ValidationResult validateMember(Member member) {
        ValidationResult result = new ValidationResult();

        if (member == null) {
            result.addError("Member cannot be null");
            return result;
        }

        validateName(member.getFirstName(), "First name", result);
        validateName(member.getLastName(), "Last name", result);
        validateEmail(member.getEmail(), result);
        
        if (member.getPassword() != null) {
            validatePassword(member.getPassword(), result);
        }
        
        validatePhoneNumber(member.getPhoneNumber(), result);

        return result;
    }

    // Private helper methods - fixes duplication
    private void validateName(String name, String fieldName, ValidationResult result) {
        if (ValidationUtils.isBlank(name)) {
            result.addError(fieldName + " is required");
        } else if (!ValidationUtils.isValidName(name)) {
            result.addError(fieldName + " contains invalid characters");
        }
    }

    private void validateEmail(String email, ValidationResult result) {
        if (ValidationUtils.isBlank(email)) {
            result.addError("Email is required");
        } else if (!ValidationUtils.isValidEmail(email)) {
            result.addError("Invalid email format");
        }
    }

    private void validatePassword(String password, ValidationResult result) {
        if (!ValidationUtils.isValidPassword(password)) {
            result.addError("Password must be at least 6 characters long");
        }
    }

    private void validatePhoneNumber(String phoneNumber, ValidationResult result) {
        if (ValidationUtils.isNotBlank(phoneNumber) && !ValidationUtils.isValidPhoneNumber(phoneNumber)) {
            result.addError("Invalid phone number format");
        }
    }

    /**
     * Validation result class to encapsulate validation outcome.
     * Fixes: Long Parameter Lists by grouping related data.
     */
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();

        public void addError(String error) {
            errors.add(error);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public String getErrorMessage() {
            return String.join(", ", errors);
        }
    }
}