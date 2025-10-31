package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Institution;
import com.turningpoint.chapterorganizer.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;

    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    public Optional<Institution> getInstitutionById(Long id) {
        return institutionRepository.findById(id);
    }

    public Institution createInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }

    public Institution updateInstitution(Long id, Institution updatedInstitution) {
        return institutionRepository.findById(id)
                .map(institution -> {
                    institution.setName(updatedInstitution.getName());
                    institution.setType(updatedInstitution.getType());
                    institution.setLocation(updatedInstitution.getLocation());
                    institution.setState(updatedInstitution.getState());
                    institution.setCountry(updatedInstitution.getCountry());
                    institution.setWebsite(updatedInstitution.getWebsite());
                    institution.setStudentCount(updatedInstitution.getStudentCount());
                    return institutionRepository.save(institution);
                })
                .orElseThrow(() -> new RuntimeException("Institution not found with id " + id));
    }

    public void deleteInstitution(Long id) {
        institutionRepository.deleteById(id);
    }

    public List<Institution> findByState(String state) {
        return institutionRepository.findByState(state);
    }

    public List<Institution> findByType(String type) {
        return institutionRepository.findByType(type);
    }

    public List<Institution> searchByName(String name) {
        return institutionRepository.findByNameContainingIgnoreCase(name);
    }
}