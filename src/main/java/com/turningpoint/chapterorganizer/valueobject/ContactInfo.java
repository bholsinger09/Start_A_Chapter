package com.turningpoint.chapterorganizer.valueobject;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Contact Information Value Object
 * Immutable object representing contact details following Clean Code principles.
 * This is a pure data structure with validation behavior - not an anemic object.
 */
@Embeddable
public final class ContactInfo {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[\\d\\s\\-\\(\\)\\+\\.]{10,15}$"
    );
    
    private final String email;
    private final String phoneNumber;

    // Constructor for JPA
    protected ContactInfo() {
        this.email = "";
        this.phoneNumber = "";
    }
    
    // Null Object pattern - represents empty/unknown contact info
    public static ContactInfo createEmpty() {
        return new ContactInfo();
    }

    public ContactInfo(String email, String phoneNumber) {
        validateEmail(email);
        validatePhoneNumber(phoneNumber);
        
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
    }
    
    public boolean isEmpty() {
        return (email == null || email.trim().isEmpty()) && 
               (phoneNumber == null || phoneNumber.trim().isEmpty());
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
                throw new IllegalArgumentException("Invalid phone number format: " + phoneNumber);
            }
        }
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    public String getFormattedPhoneNumber() {
        if (!hasPhoneNumber()) {
            return "No phone provided";
        }
        // Simple formatting - could be enhanced based on business needs
        return phoneNumber.replaceAll("\\D", "")
                .replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "($1) $2-$3");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactInfo that = (ContactInfo) o;
        return Objects.equals(email, that.email) && 
               Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phoneNumber);
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}