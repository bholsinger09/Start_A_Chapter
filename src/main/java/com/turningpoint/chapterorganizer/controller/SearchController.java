package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.dto.SearchResultsDto;
import com.turningpoint.chapterorganizer.dto.RecommendationDto;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Global search across all chapters with advanced filtering
     * Supports text search, filters, and intelligent ranking
     */
    @GetMapping("/global")
    public ResponseEntity<SearchResultsDto> globalSearch(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) Integer minMembers,
            @RequestParam(required = false) Integer maxMembers,
            @RequestParam(required = false) Integer minHealthScore,
            @RequestParam(required = false) Integer maxHealthScore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "relevance") String sortBy) {
        
        SearchResultsDto results = searchService.performGlobalSearch(
            query, states, statuses, minMembers, maxMembers, 
            minHealthScore, maxHealthScore, page, size, sortBy
        );
        
        return ResponseEntity.ok(results);
    }

    /**
     * Get personalized recommendations for a specific member
     * Based on member activity, chapter preferences, and similarities
     */
    @GetMapping("/recommendations/{memberId}")
    public ResponseEntity<List<RecommendationDto>> getPersonalizedRecommendations(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<RecommendationDto> recommendations = searchService.getPersonalizedRecommendations(memberId, limit);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get general recommendations for anonymous users or when no member ID available
     * Returns trending and high-rated chapters
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendationDto>> getGeneralRecommendations(
            @RequestParam(defaultValue = "10") int limit) {
        
        // Use null memberId to get general recommendations
        List<RecommendationDto> recommendations = searchService.getPersonalizedRecommendations(null, limit);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Find chapters similar to a given chapter
     * Based on location, size, activity patterns, and focus areas
     */
    @GetMapping("/similar-chapters/{chapterId}")
    public ResponseEntity<List<Chapter>> findSimilarChapters(
            @PathVariable Long chapterId,
            @RequestParam(defaultValue = "5") int limit) {
        
        List<Chapter> similarChapters = searchService.findSimilarChapters(chapterId, limit);
        return ResponseEntity.ok(similarChapters);
    }

    /**
     * Get search suggestions and auto-complete
     * Provides intelligent search suggestions as user types
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(
            @RequestParam String partial,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<String> suggestions = searchService.getSearchSuggestions(partial, limit);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get trending search terms and popular chapters
     * Helps users discover what others are searching for
     */
    @GetMapping("/trending")
    public ResponseEntity<Map<String, Object>> getTrendingData() {
        Map<String, Object> trendingData = searchService.getTrendingData();
        return ResponseEntity.ok(trendingData);
    }

    /**
     * Advanced faceted search with aggregated filter options
     * Returns both results and available filter facets
     */
    @GetMapping("/faceted")
    public ResponseEntity<Map<String, Object>> facetedSearch(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Map<String, String> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Map<String, Object> facetedResults = searchService.performFacetedSearch(query, filters, page, size);
        return ResponseEntity.ok(facetedResults);
    }

    /**
     * Geographic search - find chapters by location radius
     * Useful for finding nearby chapters or regional clustering
     */
    @GetMapping("/geographic")
    public ResponseEntity<List<Chapter>> searchByLocation(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "50") double radiusKm,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<Chapter> nearbyChapters = searchService.searchByLocation(latitude, longitude, radiusKm, limit);
        return ResponseEntity.ok(nearbyChapters);
    }

    /**
     * Save search query for future reference and trending analysis
     */
    @PostMapping("/save-query")
    public ResponseEntity<String> saveSearchQuery(
            @RequestParam String query,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Map<String, String> filters) {
        
        searchService.saveSearchQuery(query, userId, filters);
        return ResponseEntity.ok("Search query saved successfully");
    }

    /**
     * Get saved searches for a user
     */
    @GetMapping("/saved/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getSavedSearches(@PathVariable Long userId) {
        List<Map<String, Object>> savedSearches = searchService.getSavedSearches(userId);
        return ResponseEntity.ok(savedSearches);
    }
}