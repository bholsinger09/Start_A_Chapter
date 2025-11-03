package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    private final ChapterService chapterService;

    @Autowired
    public SearchController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    // GET /api/search/recommendations - Get recommended chapters
    @GetMapping("/recommendations")
    public ResponseEntity<List<Map<String, Object>>> getRecommendations(
            @RequestParam(defaultValue = "3") int limit) {
        try {
            // Get all chapters and return the most recently created ones as "recommendations"
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
                recommendation.put("memberCount", 0); // Default for now - we'll add member counting later
                recommendation.put("type", "chapter");
                recommendations.add(recommendation);
            }
            
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            // Return empty list if there's an error
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    // GET /api/search/trending - Get trending chapters
    @GetMapping("/trending")
    public ResponseEntity<List<Map<String, Object>>> getTrendingChapters(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            // Get all chapters and return them as "trending"
            List<Chapter> allChapters = chapterService.getAllChapters();
            
            // Just take the first few chapters for now and limit
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
                trendingItem.put("memberCount", 0); // Default for now
                trendingItem.put("trend", "up"); // Static for now
                trending.add(trendingItem);
            }
            
            return ResponseEntity.ok(trending);
        } catch (Exception e) {
            // Return empty list if there's an error
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    // GET /api/search/global - Global search across chapters, members, etc.
    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> globalSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> results = new HashMap<>();
            
            // Search chapters by name or university
            List<Chapter> chapters = chapterService.getAllChapters().stream()
                .filter(chapter -> 
                    chapter.getName().toLowerCase().contains(query.toLowerCase()) ||
                    chapter.getUniversityName().toLowerCase().contains(query.toLowerCase()) ||
                    chapter.getState().toLowerCase().contains(query.toLowerCase())
                )
                .limit(limit)
                .toList();
            
            results.put("chapters", chapters);
            results.put("totalResults", chapters.size());
            results.put("query", query);
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            Map<String, Object> errorResults = new HashMap<>();
            errorResults.put("chapters", new ArrayList<>());
            errorResults.put("totalResults", 0);
            errorResults.put("query", query);
            errorResults.put("error", "Search failed");
            return ResponseEntity.ok(errorResults);
        }
    }
}
