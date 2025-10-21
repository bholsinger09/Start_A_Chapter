package com.turningpoint.chapterorganizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateInstitutionRequest {
    
    @NotNull(message = "Institution type is required")
    private String institutionType; // "UNIVERSITY" or "CHURCH"

    @NotBlank(message = "Institution name is required")
    @Size(min = 2, max = 150, message = "Institution name must be between 2 and 150 characters")
    private String name;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    private String state;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    @Size(max = 20, message = "ZIP code cannot exceed 20 characters")
    private String zipCode;

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // University-specific fields
    @Size(max = 100, message = "University type cannot exceed 100 characters")
    private String universityType; // e.g., "Public", "Private", "Community College"

    @Size(max = 50, message = "Accreditation cannot exceed 50 characters")
    private String accreditation;

    private Integer studentPopulation;

    private Integer universityFoundedYear;

    // Church-specific fields
    @Size(max = 100, message = "Denomination cannot exceed 100 characters")
    private String denomination; // e.g., "Baptist", "Methodist", "Catholic"

    @Size(max = 100, message = "Pastor name cannot exceed 100 characters")
    private String pastorName;

    private Integer membershipSize;

    private Integer churchFoundedYear;

    @Size(max = 200, message = "Website URL cannot exceed 200 characters")
    private String website;

    // Default constructor
    public CreateInstitutionRequest() {}

    // Getters and Setters
    public String getInstitutionType() {
        return institutionType;
    }

    public void setInstitutionType(String institutionType) {
        this.institutionType = institutionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUniversityType() {
        return universityType;
    }

    public void setUniversityType(String universityType) {
        this.universityType = universityType;
    }

    public String getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(String accreditation) {
        this.accreditation = accreditation;
    }

    public Integer getStudentPopulation() {
        return studentPopulation;
    }

    public void setStudentPopulation(Integer studentPopulation) {
        this.studentPopulation = studentPopulation;
    }

    public Integer getUniversityFoundedYear() {
        return universityFoundedYear;
    }

    public void setUniversityFoundedYear(Integer universityFoundedYear) {
        this.universityFoundedYear = universityFoundedYear;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getPastorName() {
        return pastorName;
    }

    public void setPastorName(String pastorName) {
        this.pastorName = pastorName;
    }

    public Integer getMembershipSize() {
        return membershipSize;
    }

    public void setMembershipSize(Integer membershipSize) {
        this.membershipSize = membershipSize;
    }

    public Integer getChurchFoundedYear() {
        return churchFoundedYear;
    }

    public void setChurchFoundedYear(Integer churchFoundedYear) {
        this.churchFoundedYear = churchFoundedYear;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isUniversity() {
        return "UNIVERSITY".equalsIgnoreCase(institutionType);
    }

    public boolean isChurch() {
        return "CHURCH".equalsIgnoreCase(institutionType);
    }

    @Override
    public String toString() {
        return "CreateInstitutionRequest{" +
                "institutionType='" + institutionType + '\'' +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}