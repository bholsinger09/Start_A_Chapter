package com.turningpoint.chapterorganizer.dto;

import com.turningpoint.chapterorganizer.entity.MemberRole;

public class MemberDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phoneNumber;
    private MemberRole role;
    private Boolean active;
    private String major;
    private String graduationYear;
    
    // Chapter information (flattened to avoid circular references)
    private Long chapterId;
    private String chapterName;
    private String universityName;
    private String state;
    private String city;
    
    // Constructors
    public MemberDTO() {}
    
    public MemberDTO(Long id, String firstName, String lastName, String email, String username,
                     String phoneNumber, MemberRole role, Boolean active, String major, 
                     String graduationYear, Long chapterId, String chapterName, 
                     String universityName, String state, String city) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.active = active;
        this.major = major;
        this.graduationYear = graduationYear;
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.universityName = universityName;
        this.state = state;
        this.city = city;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public MemberRole getRole() {
        return role;
    }
    
    public void setRole(MemberRole role) {
        this.role = role;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
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
    
    public Long getChapterId() {
        return chapterId;
    }
    
    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }
    
    public String getChapterName() {
        return chapterName;
    }
    
    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
    
    public String getUniversityName() {
        return universityName;
    }
    
    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
}