package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Institution;
import com.turningpoint.chapterorganizer.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/institutions")
public class InstitutionController {

    private static final Logger logger = LoggerFactory.getLogger(InstitutionController.class);

    @Autowired
    private InstitutionService institutionService;

    @GetMapping
    public ResponseEntity<List<Institution>> getAllInstitutions(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String exclude) {
        try {
            logger.info("📋 INSTITUTIONS REQUEST - Parameters: state={}, type={}, exclude={}", state, type, exclude);
            
            List<Institution> institutions;
            
            if (state != null && !state.isEmpty()) {
                // Filter by specific state if provided
                logger.info("📍 Filtering institutions by state: {}", state);
                institutions = institutionService.findByState(state);
                logger.info("📊 Found {} institutions for state: {}", institutions.size(), state);
            } else if (type != null && !type.isEmpty()) {
                // Filter by type if provided
                logger.info("🏫 Filtering institutions by type: {}", type);
                institutions = institutionService.findByType(type);
                logger.info("📊 Found {} institutions for type: {}", institutions.size(), type);
            } else {
                // Get all institutions but apply exclusion filter
                logger.info("🏢 Getting all institutions with exclusion filter");
                institutions = institutionService.getAllInstitutions();
                logger.info("📊 Total institutions before filtering: {}", institutions.size());
                
                if (exclude != null && !exclude.isEmpty()) {
                    // Exclude specific states (comma-separated list)
                    List<String> excludeStates = List.of(exclude.split(","));
                    logger.info("🚫 Excluding states: {}", excludeStates);
                    institutions = institutions.stream()
                        .filter(institution -> !excludeStates.contains(institution.getState()))
                        .toList();
                } else {
                    // Default: exclude less commonly relevant states for general use
                    List<String> defaultExcludeStates = List.of("AK", "HI", "WY", "ND", "SD", "VT", "DE", "RI", "DC");
                    logger.info("🚫 Applying default state exclusions: {}", defaultExcludeStates);
                    institutions = institutions.stream()
                        .filter(institution -> !defaultExcludeStates.contains(institution.getState()))
                        .toList();
                }
                logger.info("📊 Final institutions count after filtering: {}", institutions.size());
            }
            
            logger.info("✅ Successfully returning {} institutions", institutions.size());
            return ResponseEntity.ok(institutions);
        } catch (Exception e) {
            logger.error("❌ INSTITUTIONS ERROR: Failed to fetch institutions", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Institution> getInstitutionById(@PathVariable Long id) {
        Optional<Institution> institution = institutionService.getInstitutionById(id);
        return institution.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Institution> createInstitution(@RequestBody Institution institution) {
        Institution createdInstitution = institutionService.createInstitution(institution);
        return ResponseEntity.ok(createdInstitution);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Institution> updateInstitution(@PathVariable Long id, @RequestBody Institution institution) {
        try {
            Institution updatedInstitution = institutionService.updateInstitution(id, institution);
            return ResponseEntity.ok(updatedInstitution);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstitution(@PathVariable Long id) {
        institutionService.deleteInstitution(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Institution>> searchInstitutions(@RequestParam String name) {
        List<Institution> institutions = institutionService.searchByName(name);
        return ResponseEntity.ok(institutions);
    }

    @GetMapping("/by-state/{state}")
    public ResponseEntity<List<Institution>> getInstitutionsByState(@PathVariable String state) {
        List<Institution> institutions = institutionService.findByState(state);
        return ResponseEntity.ok(institutions);
    }

    @GetMapping("/by-type/{type}")
    public ResponseEntity<List<Institution>> getInstitutionsByType(@PathVariable String type) {
        List<Institution> institutions = institutionService.findByType(type);
        return ResponseEntity.ok(institutions);
    }
}