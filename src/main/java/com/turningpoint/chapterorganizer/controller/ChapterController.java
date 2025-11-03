package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Institution;
import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;
    
    @Autowired
    private InstitutionService institutionService;

    @GetMapping("/chapters")
    public ResponseEntity<List<Chapter>> getAllChapters() {
        try {
            List<Chapter> chapters = chapterService.getAllActiveChapters();
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // IMPORTANT: This must come before /chapters/{id} to avoid routing conflict
    @GetMapping("/chapters/institutions")
    public ResponseEntity<List<Institution>> getChapterInstitutions() {
        try {
            List<Institution> institutions = institutionService.getAllInstitutions();
            return ResponseEntity.ok(institutions);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/chapters/{id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable Long id) {
        try {
            Optional<Chapter> chapter = chapterService.getChapterById(id);
            return chapter.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/chapters")
    public ResponseEntity<Chapter> createChapter(@RequestBody Chapter chapter) {
        try {
            Chapter createdChapter = chapterService.createChapter(chapter);
            return ResponseEntity.ok(createdChapter);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable Long id, @RequestBody Chapter chapter) {
        try {
            Chapter updatedChapter = chapterService.updateChapter(id, chapter);
            return ResponseEntity.ok(updatedChapter);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        try {
            chapterService.deleteChapter(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Search endpoints - temporary solution until SearchController is deployed
    @GetMapping("/search/trending")
    public ResponseEntity<List<Map<String, Object>>> getTrendingChapters(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<Chapter> allChapters = chapterService.getAllChapters();
            
            // Take the first few chapters as "trending"
            List<Chapter> trendingChapters = allChapters.stream()
                .limit(limit)
                .toList();
            
            // Convert to simplified format for frontend
            List<Map<String, Object>> trending = new ArrayList<>();
            for (Chapter chapter : trendingChapters) {
                Map<String, Object> trendingItem = new HashMap<>();
                trendingItem.put("id", chapter.getId());
                trendingItem.put("name", chapter.getName());
                trendingItem.put("university", chapter.getUniversityName());
                trendingItem.put("state", chapter.getState());
                trendingItem.put("memberCount", 0);
                trendingItem.put("trend", "up");
                trending.add(trendingItem);
            }
            
            return ResponseEntity.ok(trending);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/search/recommendations")
    public ResponseEntity<List<Map<String, Object>>> getRecommendations(
            @RequestParam(defaultValue = "3") int limit) {
        try {
            List<Chapter> allChapters = chapterService.getAllChapters();
            
            // Sort by creation date (newest first) and limit
            List<Chapter> recommendedChapters = allChapters.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .toList();
            
            // Convert to simplified format for frontend
            List<Map<String, Object>> recommendations = new ArrayList<>();
            for (Chapter chapter : recommendedChapters) {
                Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("id", chapter.getId());
                recommendation.put("name", chapter.getName());
                recommendation.put("university", chapter.getUniversityName());
                recommendation.put("state", chapter.getState());
                recommendation.put("memberCount", 0);
                recommendation.put("type", "chapter");
                recommendations.add(recommendation);
            }
            
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    // Institutions endpoint for chapter creation form
    @GetMapping("/institutions")
    public ResponseEntity<List<Institution>> getInstitutions() {
        try {
            List<Institution> institutions = institutionService.getAllInstitutions();
            return ResponseEntity.ok(institutions);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
