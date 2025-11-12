package com.turningpoint.chapterorganizer.dto;

/**
 * DTO for registration requests following Robert Martin's Clean Code principles.
 * 
 * This class demonstrates:
 * 1. Single Responsibility - Only handles registration data
 * 2. Data Structure - Simple data holder with getters/setters
 * 3. Encapsulation - Private fields with public accessors
 */
public class RegistrationRequest {
    
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phoneNumber;
    private String major;
    private String graduationYear;
    private String password;
    private Long chapterId;

    // Default constructor
    public RegistrationRequest() {
    }

    // Constructor with required fields
    public RegistrationRequest(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", chapterId=" + chapterId +
                '}';
    }
}