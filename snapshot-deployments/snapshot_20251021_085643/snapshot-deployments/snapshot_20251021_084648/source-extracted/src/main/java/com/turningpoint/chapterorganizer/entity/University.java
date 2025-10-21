package com.turningpoint.chapterorganizer.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;

@Entity
@DiscriminatorValue("UNIVERSITY")
public class University extends Institution {

    @Size(max = 100, message = "University type cannot exceed 100 characters")
    private String universityType; // e.g., "Public", "Private", "Community College"

    @Size(max = 50, message = "Accreditation cannot exceed 50 characters")
    private String accreditation;

    private Integer studentPopulation;

    private Integer foundedYear;

    // Constructors
    public University() {
        super();
    }

    public University(String name, String state, String city) {
        super(name, state, city);
    }

    public University(String name, String state, String city, String universityType, Integer studentPopulation) {
        super(name, state, city);
        this.universityType = universityType;
        this.studentPopulation = studentPopulation;
    }

    // Getters and Setters
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

    public Integer getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
    }

    @Override
    public String getInstitutionType() {
        return "UNIVERSITY";
    }

    @Override
    public String toString() {
        return "University{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", universityType='" + universityType + '\'' +
                ", studentPopulation=" + studentPopulation +
                ", state='" + getState() + '\'' +
                ", city='" + getCity() + '\'' +
                '}';
    }
}