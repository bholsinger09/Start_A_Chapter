package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Institution;
import com.turningpoint.chapterorganizer.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/institutions")
public class InstitutionController {

    @Autowired
    private InstitutionService institutionService;

    @GetMapping
    public ResponseEntity<List<Institution>> getAllInstitutions() {
        List<Institution> institutions = institutionService.getAllInstitutions();
        return ResponseEntity.ok(institutions);
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