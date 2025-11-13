package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.service.ChapterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@RestController
@RequestMapping("/api/chapters")
@CrossOrigin(
    origins = {"https://startachapter.duckdns.org", "http://startachapter.duckdns.org", "*"}, 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "false"
)
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("")
    public ResponseEntity<List<Chapter>> getAllChapters() {
        try {
            List<Chapter> chapters = chapterService.getAllActiveChapters();
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable Long id) {
        try {
            Optional<Chapter> chapter = chapterService.getChapterById(id);
            return chapter.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("")
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

    /**
     * Helper method to convert Chapter entity to simplified Map format for frontend
     * Extracts repetitive data transformation logic following DRY principle
     */
    private Map<String, Object> convertChapterToSearchResult(Chapter chapter, String extraField, Object extraValue) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", chapter.getId());
        result.put("name", chapter.getName());
        result.put("university", chapter.getUniversityName());
        result.put("state", chapter.getState());
        result.put("memberCount", 0);
        if (extraField != null && extraValue != null) {
            result.put(extraField, extraValue);
        }
        return result;
    }

    /**
     * Helper method to convert list of chapters to search result format
     * Centralizes chapter data transformation for consistency
     */
    private List<Map<String, Object>> convertChaptersToSearchResults(
            List<Chapter> chapters, String extraField, Object extraValue) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (Chapter chapter : chapters) {
            results.add(convertChapterToSearchResult(chapter, extraField, extraValue));
        }
        return results;
    }

    @GetMapping("/search/trending")
    public ResponseEntity<List<Map<String, Object>>> getTrendingChapters(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<Chapter> allChapters = chapterService.getAllChapters();
            
            // Take the first few chapters as "trending"
            List<Chapter> trendingChapters = allChapters.stream()
                .limit(limit)
                .toList();
            
            // Convert to simplified format for frontend using helper method
            List<Map<String, Object>> trending = convertChaptersToSearchResults(trendingChapters, "trend", "up");
            
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
            
            // Convert to simplified format for frontend using helper method
            List<Map<String, Object>> recommendations = convertChaptersToSearchResults(
                    recommendedChapters, "type", "chapter");
            
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }


}
