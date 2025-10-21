package com.turningpoint.chapterorganizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateChapterRequest {
    
    @NotBlank(message = "Chapter name is required")
    @Size(min = 2, max = 100, message = "Chapter name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // Option 1: Reference existing institution
    private Long institutionId;

    // Option 2: Create new institution
    private CreateInstitutionRequest newInstitution;

    // Default constructor
    public CreateChapterRequest() {}

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public CreateInstitutionRequest getNewInstitution() {
        return newInstitution;
    }

    public void setNewInstitution(CreateInstitutionRequest newInstitution) {
        this.newInstitution = newInstitution;
    }

    // Validation method
    public boolean hasValidInstitution() {
        return institutionId != null || newInstitution != null;
    }

    @Override
    public String toString() {
        return "CreateChapterRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", institutionId=" + institutionId +
                ", newInstitution=" + newInstitution +
                '}';
    }
}