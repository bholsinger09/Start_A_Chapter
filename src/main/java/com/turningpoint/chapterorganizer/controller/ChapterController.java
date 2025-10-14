package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.service.ChapterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chapters")
@CrossOrigin(origins = "*")
public class ChapterController {

    private final ChapterService chapterService;

    @Autowired
    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    // GET /api/chapters - Get all chapters
    @GetMapping
    public ResponseEntity<List<Chapter>> getAllChapters() {
        List<Chapter> chapters = chapterService.getAllChapters();
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
        List<Chapter> chapters = chapterService.searchChapters(name, university, state, city, active);
        return ResponseEntity.ok(chapters);
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