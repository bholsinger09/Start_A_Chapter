package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.*;
import com.turningpoint.chapterorganizer.repository.ChurchRepository;
import com.turningpoint.chapterorganizer.repository.InstitutionRepository;
import com.turningpoint.chapterorganizer.repository.UniversityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private ChurchRepository churchRepository;

    // Institution methods
    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    public Optional<Institution> getInstitutionById(Long id) {
        return institutionRepository.findById(id);
    }

    public Optional<Institution> getInstitutionByName(String name) {
        return institutionRepository.findByNameIgnoreCase(name);
    }

    public List<Institution> searchInstitutions(String searchTerm) {
        return institutionRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    public List<Institution> getInstitutionsByState(String state) {
        return institutionRepository.findByStateIgnoreCase(state);
    }

    public boolean institutionExists(String name) {
        return institutionRepository.existsByNameIgnoreCase(name);
    }

    public void deleteInstitution(Long id) {
        institutionRepository.deleteById(id);
    }

    // University methods
    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }

    public Optional<University> getUniversityById(Long id) {
        return universityRepository.findById(id);
    }

    public Optional<University> getUniversityByName(String name) {
        return universityRepository.findByNameIgnoreCase(name);
    }

    public University createUniversity(University university) {
        if (institutionRepository.existsByNameIgnoreCase(university.getName())) {
            throw new IllegalArgumentException("Institution with this name already exists");
        }
        return universityRepository.save(university);
    }

    public University updateUniversity(Long id, University universityDetails) {
        University university = universityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("University not found with id: " + id));

        university.setName(universityDetails.getName());
        university.setState(universityDetails.getState());
        university.setCity(universityDetails.getCity());
        university.setZipCode(universityDetails.getZipCode());
        university.setAddress(universityDetails.getAddress());
        university.setDescription(universityDetails.getDescription());
        university.setUniversityType(universityDetails.getUniversityType());
        university.setAccreditation(universityDetails.getAccreditation());
        university.setStudentPopulation(universityDetails.getStudentPopulation());
        university.setFoundedYear(universityDetails.getFoundedYear());

        return universityRepository.save(university);
    }

    public List<University> searchUniversities(String searchTerm) {
        return universityRepository.searchByName(searchTerm);
    }

    public List<University> getUniversitiesByType(String universityType) {
        return universityRepository.findByUniversityTypeIgnoreCase(universityType);
    }

    // Church methods
    public List<Church> getAllChurches() {
        return churchRepository.findAll();
    }

    public Optional<Church> getChurchById(Long id) {
        return churchRepository.findById(id);
    }

    public Optional<Church> getChurchByName(String name) {
        return churchRepository.findByNameIgnoreCase(name);
    }

    public Church createChurch(Church church) {
        if (institutionRepository.existsByNameIgnoreCase(church.getName())) {
            throw new IllegalArgumentException("Institution with this name already exists");
        }
        return churchRepository.save(church);
    }

    public Church updateChurch(Long id, Church churchDetails) {
        Church church = churchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Church not found with id: " + id));

        church.setName(churchDetails.getName());
        church.setState(churchDetails.getState());
        church.setCity(churchDetails.getCity());
        church.setZipCode(churchDetails.getZipCode());
        church.setAddress(churchDetails.getAddress());
        church.setDescription(churchDetails.getDescription());
        church.setDenomination(churchDetails.getDenomination());
        church.setPastorName(churchDetails.getPastorName());
        church.setMembershipSize(churchDetails.getMembershipSize());
        church.setFoundedYear(churchDetails.getFoundedYear());
        church.setWebsite(churchDetails.getWebsite());

        return churchRepository.save(church);
    }

    public List<Church> searchChurches(String searchTerm) {
        return churchRepository.searchByName(searchTerm);
    }

    public List<Church> getChurchesByDenomination(String denomination) {
        return churchRepository.findByDenominationIgnoreCase(denomination);
    }

    public List<String> getAllDenominations() {
        return churchRepository.findDistinctDenominations();
    }
}