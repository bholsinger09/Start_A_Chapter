package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.service.UniversityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/universities")
@CrossOrigin(origins = {"http://localhost:3000", "https://startachapter.duckdns.org"})
public class UniversityController {

    private final UniversityService universityService;

    public UniversityController(UniversityService universityService) {
        this.universityService = universityService;
    }

    @GetMapping
    public ResponseEntity<Map<String, List<String>>> getAllUniversitiesByState() {
        return ResponseEntity.ok(universityService.getAllUniversitiesByState());
    }

    @GetMapping("/states/{state}")
    public ResponseEntity<List<String>> getUniversitiesByState(@PathVariable String state) {
        List<String> universities = universityService.getUniversitiesByState(state);
        return ResponseEntity.ok(universities);
    }

    @GetMapping("/states")
    public ResponseEntity<List<String>> getAllStates() {
        return ResponseEntity.ok(universityService.getAllStates());
    }
}