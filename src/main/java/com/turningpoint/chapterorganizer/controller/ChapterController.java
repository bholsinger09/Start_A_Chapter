package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.dto.CreateChapterRequest;
import com.turningpoint.chapterorganizer.dto.CreateInstitutionRequest;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Institution;
import com.turningpoint.chapterorganizer.entity.University;
import com.turningpoint.chapterorganizer.entity.Church;
import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.InstitutionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/chapters")
@CrossOrigin(origins = "*")
public class ChapterController {

    private final ChapterService chapterService;
    private final InstitutionService institutionService;

    @Autowired
    public ChapterController(ChapterService chapterService, InstitutionService institutionService) {
        this.chapterService = chapterService;
        this.institutionService = institutionService;
    }

    // GET /api/chapters - Get all chapters
    @GetMapping
    public ResponseEntity<List<Chapter>> getAllChapters() {
        try {
            System.out.println("DEBUG: ChapterController.getAllChapters() called");
            List<Chapter> chapters = chapterService.getAllActiveChapters();
            System.out.println("DEBUG: Retrieved " + chapters.size() + " chapters");
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            System.err.println("ERROR in getAllChapters: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // GET /api/chapters/paginated - Get paginated chapters with sorting
    @GetMapping("/paginated")
    public ResponseEntity<Page<Chapter>> getPaginatedChapters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Chapter> chapters = chapterService.getPaginatedChapters(pageable);
        return ResponseEntity.ok(chapters);
    }

    // GET /api/chapters/active - Get all active chapters
    @GetMapping("/active")
    public ResponseEntity<List<Chapter>> getActiveChapters() {
        List<Chapter> activeChapters = chapterService.getAllActiveChapters();
        return ResponseEntity.ok(activeChapters);
    }

    // GET /api/chapters/{id} - Get chapter by ID
    @GetMapping("/{id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable Long id) {
        Optional<Chapter> chapter = chapterService.getChapterById(id);
        return chapter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/chapters/search - Search chapters by name or university
    @GetMapping("/search")
    public ResponseEntity<List<Chapter>> searchChapters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String university,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean active) {
        try {
            System.out.println("Search request received - name: " + name + 
                             ", university: " + university + 
                             ", state: " + state + 
                             ", city: " + city + 
                             ", active: " + active);
            
            List<Chapter> chapters = chapterService.searchChapters(name, university, state, city, active);
            
            System.out.println("Search completed successfully, returning " + chapters.size() + " chapters");
            return ResponseEntity.ok(chapters);
            
        } catch (Exception e) {
            System.err.println("Error in searchChapters endpoint: " + e.getMessage());
            e.printStackTrace();
            
            // Return empty list instead of error to prevent frontend crashes
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    // POST /api/chapters - Create new chapter
    @PostMapping
    public ResponseEntity<Chapter> createChapter(@Valid @RequestBody Chapter chapter) {
        try {
            Chapter createdChapter = chapterService.createChapter(chapter);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdChapter);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/chapters/{id} - Update existing chapter
    @PutMapping("/{id}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable Long id, 
                                               @Valid @RequestBody Chapter chapterDetails) {
        try {
            Chapter updatedChapter = chapterService.updateChapter(id, chapterDetails);
            return ResponseEntity.ok(updatedChapter);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/chapters/{id} - Delete chapter (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        try {
            chapterService.deleteChapter(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/chapters/{id}/activate - Activate chapter
    @PutMapping("/{id}/activate")
    public ResponseEntity<Chapter> activateChapter(@PathVariable Long id) {
        try {
            Chapter chapter = chapterService.reactivateChapter(id);
            return ResponseEntity.ok(chapter);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/chapters/state/{state} - Get chapters by state
    @GetMapping("/state/{state}")
    public ResponseEntity<List<Chapter>> getChaptersByState(@PathVariable String state) {
        List<Chapter> chapters = chapterService.searchChaptersByState(state);
        return ResponseEntity.ok(chapters);
    }

    // GET /api/chapters/{id}/stats - Get chapter statistics
    @GetMapping("/{id}/stats")
    public ResponseEntity<ChapterStatsDto> getChapterStats(@PathVariable Long id) {
        try {
            Optional<Chapter> chapter = chapterService.getChapterById(id);
            if (chapter.isPresent()) {
                Chapter c = chapter.get();
                ChapterStatsDto stats = new ChapterStatsDto(
                    c.getName(),
                    c.getActive(),
                    c.getUniversityName(),
                    c.getState(),
                    c.getCity()
                );
                return ResponseEntity.ok(stats);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST /api/chapters/with-institution - Create new chapter with institution
    @PostMapping("/with-institution")
    public ResponseEntity<?> createChapterWithInstitution(@Valid @RequestBody CreateChapterRequest request) {
        try {
            System.out.println("DEBUG: createChapterWithInstitution called");
            System.out.println("DEBUG: Request object: " + request);
            System.out.println("DEBUG: Request name: " + (request != null ? request.getName() : "null"));
            System.out.println("DEBUG: Request description: " + (request != null ? request.getDescription() : "null"));
            System.out.println("DEBUG: Request institutionId: " + (request != null ? request.getInstitutionId() : "null"));
            System.out.println("DEBUG: Request newInstitution: " + (request != null ? request.getNewInstitution() : "null"));
            
            if (request == null) {
                System.err.println("ERROR: Request is null");
                return ResponseEntity.badRequest().build();
            }
            
            System.out.println("DEBUG: Request validation hasValidInstitution: " + request.hasValidInstitution());
            
            if (!request.hasValidInstitution()) {
                System.err.println("ERROR: Invalid institution - both institutionId and newInstitution are null");
                return ResponseEntity.badRequest().build();
            }

            Institution institution;
            
            if (request.getInstitutionId() != null) {
                System.out.println("DEBUG: Using existing institution ID: " + request.getInstitutionId());
                // Use existing institution
                Optional<Institution> existingInstitution = institutionService.getInstitutionById(request.getInstitutionId());
                if (existingInstitution.isEmpty()) {
                    System.err.println("ERROR: Institution not found with ID: " + request.getInstitutionId());
                    return ResponseEntity.badRequest().build();
                }
                institution = existingInstitution.get();
            } else {
                System.out.println("DEBUG: Creating new institution: " + request.getNewInstitution());
                // Create new institution
                CreateInstitutionRequest newInstRequest = request.getNewInstitution();
                if (newInstRequest == null) {
                    System.err.println("ERROR: newInstitution is null");
                    return ResponseEntity.badRequest().build();
                }
                
                if (newInstRequest.isUniversity()) {
                    University university = new University();
                    university.setName(newInstRequest.getName());
                    university.setState(newInstRequest.getState());
                    university.setCity(newInstRequest.getCity());
                    university.setZipCode(newInstRequest.getZipCode());
                    university.setAddress(newInstRequest.getAddress());
                    university.setDescription(newInstRequest.getDescription());
                    university.setUniversityType(newInstRequest.getUniversityType());
                    university.setAccreditation(newInstRequest.getAccreditation());
                    university.setStudentPopulation(newInstRequest.getStudentPopulation());
                    university.setFoundedYear(newInstRequest.getUniversityFoundedYear());
                    institution = institutionService.createUniversity(university);
                } else if (newInstRequest.isChurch()) {
                    Church church = new Church();
                    church.setName(newInstRequest.getName());
                    church.setState(newInstRequest.getState());
                    church.setCity(newInstRequest.getCity());
                    church.setZipCode(newInstRequest.getZipCode());
                    church.setAddress(newInstRequest.getAddress());
                    church.setDescription(newInstRequest.getDescription());
                    church.setDenomination(newInstRequest.getDenomination());
                    church.setPastorName(newInstRequest.getPastorName());
                    church.setMembershipSize(newInstRequest.getMembershipSize());
                    church.setFoundedYear(newInstRequest.getChurchFoundedYear());
                    church.setWebsite(newInstRequest.getWebsite());
                    institution = institutionService.createChurch(church);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }

            // Create the chapter
            System.out.println("DEBUG: Creating chapter with name: " + request.getName());
            Chapter chapter = new Chapter(request.getName(), institution);
            chapter.setDescription(request.getDescription());
            Chapter createdChapter = chapterService.createChapter(chapter);
            System.out.println("DEBUG: Chapter created successfully with ID: " + createdChapter.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdChapter);
            
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: IllegalArgumentException in createChapterWithInstitution: " + e.getMessage());
            e.printStackTrace();
            // Return the error message in the response body so frontend can display it
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("ERROR: Exception in createChapterWithInstitution: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error occurred"));
        }
    }

    // GET /api/chapters/institutions - Get all institutions
    @GetMapping("/institutions")
    public ResponseEntity<List<Institution>> getAllInstitutions() {
        try {
            System.out.println("🔍 GET /api/chapters/institutions called");
            long startTime = System.currentTimeMillis();
            
            List<Institution> institutions = institutionService.getAllInstitutions();
            
            long endTime = System.currentTimeMillis();
            System.out.println("🔍 Found " + institutions.size() + " institutions in " + (endTime - startTime) + "ms");
            
            return ResponseEntity.ok(institutions);
        } catch (Exception e) {
            System.err.println("❌ Error getting institutions: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/chapters/institutions/universities - Get all universities
    @GetMapping("/institutions/universities")
    public ResponseEntity<List<University>> getAllUniversities() {
        List<University> universities = institutionService.getAllUniversities();
        return ResponseEntity.ok(universities);
    }

    // GET /api/chapters/institutions/churches - Get all churches
    @GetMapping("/institutions/churches")
    public ResponseEntity<List<Church>> getAllChurches() {
        List<Church> churches = institutionService.getAllChurches();
        return ResponseEntity.ok(churches);
    }

    // GET /api/chapters/institutions/search - Search institutions
    @GetMapping("/institutions/search")
    public ResponseEntity<List<Institution>> searchInstitutions(@RequestParam String query) {
        List<Institution> institutions = institutionService.searchInstitutions(query);
        return ResponseEntity.ok(institutions);
    }

    // Inner DTO class for chapter statistics
    public static class ChapterStatsDto {
        public final String name;
        public final boolean active;
        public final String university;
        public final String state;
        public final String city;

        public ChapterStatsDto(String name, boolean active, String university, String state, String city) {
            this.name = name;
            this.active = active;
            this.university = university;
            this.state = state;
            this.city = city;
        }
    }
}