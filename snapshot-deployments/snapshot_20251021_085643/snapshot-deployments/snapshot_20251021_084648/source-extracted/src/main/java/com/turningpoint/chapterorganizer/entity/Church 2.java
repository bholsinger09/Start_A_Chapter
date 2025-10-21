package com.turningpoint.chapterorganizer.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;

@Entity
@DiscriminatorValue("CHURCH")
public class Church extends Institution {

    @Size(max = 100, message = "Denomination cannot exceed 100 characters")
    private String denomination; // e.g., "Baptist", "Methodist", "Catholic", "Non-denominational"

    @Size(max = 100, message = "Pastor name cannot exceed 100 characters")
    private String pastorName;

    private Integer membershipSize;

    private Integer foundedYear;

    @Size(max = 200, message = "Website URL cannot exceed 200 characters")
    private String website;

    // Constructors
    public Church() {
        super();
    }

    public Church(String name, String state, String city) {
        super(name, state, city);
    }

    public Church(String name, String state, String city, String denomination, Integer membershipSize) {
        super(name, state, city);
        this.denomination = denomination;
        this.membershipSize = membershipSize;
    }

    // Getters and Setters
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

    public Integer getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String getInstitutionType() {
        return "CHURCH";
    }

    @Override
    public String toString() {
        return "Church{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", denomination='" + denomination + '\'' +
                ", pastorName='" + pastorName + '\'' +
                ", membershipSize=" + membershipSize +
                ", state='" + getState() + '\'' +
                ", city='" + getCity() + '\'' +
                '}';
    }
}