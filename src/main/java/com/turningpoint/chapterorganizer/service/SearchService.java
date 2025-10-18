package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.dto.RecommendationDto;
import com.turningpoint.chapterorganizer.dto.SearchResultsDto;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final ChapterRepository chapterRepository;
    private final MemberRepository memberRepository;
    
    // In-memory storage for demo - in production, use Redis or database
    private final Map<String, Integer> searchQueryCounts = new HashMap<>();
    private final Map<Long, List<Map<String, Object>>> userSavedSearches = new HashMap<>();

    @Autowired
    public SearchService(ChapterRepository chapterRepository, MemberRepository memberRepository) {
        this.chapterRepository = chapterRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * Perform intelligent global search with ranking and filtering
     */
    public SearchResultsDto performGlobalSearch(String query, List<String> states, List<String> statuses,
                                              Integer minMembers, Integer maxMembers, Integer minHealthScore,
                                              Integer maxHealthScore, int page, int size, String sortBy) {
        
        long startTime = System.currentTimeMillis();
        
        // Get all chapters and apply filtering
        List<Chapter> allChapters = chapterRepository.findAll();
        
        List<Chapter> filteredChapters = allChapters.stream()
                .filter(chapter -> matchesQuery(chapter, query))
                .filter(chapter -> {
                    boolean stateMatch = states == null || states.isEmpty() || states.contains(chapter.getState());
                    if (states != null && !states.isEmpty()) {
                        System.out.println("DEBUG: Chapter '" + chapter.getName() + "' state: '" + chapter.getState() + "' vs filter states: " + states + " = " + stateMatch);
                    }
                    return stateMatch;
                })
                .filter(chapter -> statuses == null || statuses.isEmpty() || 
                        (statuses.contains("Active") && Boolean.TRUE.equals(chapter.getActive())) ||
                        (statuses.contains("Inactive") && Boolean.FALSE.equals(chapter.getActive())))
                .filter(chapter -> minMembers == null || getMemberCount(chapter) >= minMembers)
                .filter(chapter -> maxMembers == null || getMemberCount(chapter) <= maxMembers)
                .filter(chapter -> minHealthScore == null || getHealthScore(chapter) >= minHealthScore)
                .filter(chapter -> maxHealthScore == null || getHealthScore(chapter) <= maxHealthScore)
                .collect(Collectors.toList());

        // Apply sorting
        filteredChapters = applySorting(filteredChapters, sortBy, query);

        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, filteredChapters.size());
        List<Chapter> paginatedChapters = filteredChapters.subList(start, end);

        // Generate facets
        Map<String, Object> facets = generateFacets(allChapters);

        // Generate suggestions
        List<String> suggestions = generateSuggestions(query);

        long searchTime = System.currentTimeMillis() - startTime;

        return new SearchResultsDto(
                paginatedChapters,
                filteredChapters.size(),
                (int) Math.ceil((double) filteredChapters.size() / size),
                page,
                size,
                facets,
                suggestions,
                searchTime
        );
    }

    /**
     * Get personalized recommendations based on member activity and preferences
     */
    public List<RecommendationDto> getPersonalizedRecommendations(Long memberId, int limit) {
        List<RecommendationDto> recommendations = new ArrayList<>();
        
        // Handle null member ID - return general recommendations
        if (memberId == null) {
            return getGeneralRecommendations(limit);
        }
        
        // Get member info (if exists)
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            return getGeneralRecommendations(limit);
        }
        
        Member member = memberOpt.get();
        List<Chapter> allChapters = chapterRepository.findAll();
        
        // Generate different types of recommendations
        recommendations.addAll(getLocationBasedRecommendations(member, allChapters));
        recommendations.addAll(getSizeBasedRecommendations(member, allChapters));
        recommendations.addAll(getActivityBasedRecommendations(member, allChapters));
        
        // Sort by score and limit
        return recommendations.stream()
                .sorted((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Find chapters similar to a given chapter using multiple criteria
     */
    public List<Chapter> findSimilarChapters(Long chapterId, int limit) {
        Optional<Chapter> targetChapterOpt = chapterRepository.findById(chapterId);
        if (!targetChapterOpt.isPresent()) {
            return new ArrayList<>();
        }
        
        Chapter targetChapter = targetChapterOpt.get();
        List<Chapter> allChapters = chapterRepository.findAll();
        
        // Calculate similarity scores
        Map<Chapter, Double> similarityScores = allChapters.stream()
                .filter(chapter -> !chapter.getId().equals(chapterId))
                .collect(Collectors.toMap(
                        chapter -> chapter,
                        chapter -> calculateSimilarity(targetChapter, chapter)
                ));
        
        // Sort by similarity and return top results
        return similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Chapter, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Generate search suggestions based on partial input
     */
    public List<String> getSearchSuggestions(String partial, int limit) {
        Set<String> suggestions = new HashSet<>();
        List<Chapter> allChapters = chapterRepository.findAll();
        
        String partialLower = partial.toLowerCase();
        
        // Add chapter name suggestions
        allChapters.forEach(chapter -> {
            if (chapter.getName() != null && chapter.getName().toLowerCase().contains(partialLower)) {
                suggestions.add(chapter.getName());
            }
            if (chapter.getUniversityName() != null && chapter.getUniversityName().toLowerCase().contains(partialLower)) {
                suggestions.add(chapter.getUniversityName());
            }
            if (chapter.getCity() != null && chapter.getCity().toLowerCase().contains(partialLower)) {
                suggestions.add(chapter.getCity());
            }
            if (chapter.getState() != null && chapter.getState().toLowerCase().contains(partialLower)) {
                suggestions.add(chapter.getState());
            }
        });
        
        return suggestions.stream()
                .sorted()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get trending search data
     */
    public Map<String, Object> getTrendingData() {
        Map<String, Object> trendingData = new HashMap<>();
        
        // Get top search queries
        List<Map.Entry<String, Integer>> topQueries = searchQueryCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        trendingData.put("topSearchQueries", topQueries.stream()
                .map(entry -> Map.of("query", entry.getKey(), "count", entry.getValue()))
                .collect(Collectors.toList()));
        
        // Get popular chapters (most members)
        List<Chapter> popularChapters = chapterRepository.findAll().stream()
                .sorted((c1, c2) -> Integer.compare(getMemberCount(c2), getMemberCount(c1)))
                .limit(5)
                .collect(Collectors.toList());
        
        trendingData.put("popularChapters", popularChapters);
        
        // Get newest chapters
        List<Chapter> newestChapters = chapterRepository.findAll().stream()
                .sorted((c1, c2) -> {
                    if (c1.getFoundedDate() == null && c2.getFoundedDate() == null) return 0;
                    if (c1.getFoundedDate() == null) return 1;
                    if (c2.getFoundedDate() == null) return -1;
                    return c2.getFoundedDate().compareTo(c1.getFoundedDate());
                })
                .limit(5)
                .collect(Collectors.toList());
        
        trendingData.put("newestChapters", newestChapters);
        
        return trendingData;
    }

    /**
     * Perform faceted search with aggregated filters
     */
    public Map<String, Object> performFacetedSearch(String query, Map<String, String> filters, int page, int size) {
        List<Chapter> allChapters = chapterRepository.findAll();
        
        // Apply query filter
        if (query != null && !query.trim().isEmpty()) {
            allChapters = allChapters.stream()
                    .filter(chapter -> matchesQuery(chapter, query))
                    .collect(Collectors.toList());
        }
        
        // Apply additional filters
        if (filters != null) {
            for (Map.Entry<String, String> filter : filters.entrySet()) {
                allChapters = applyFilter(allChapters, filter.getKey(), filter.getValue());
            }
        }
        
        // Generate facets from filtered results
        Map<String, Object> facets = generateFacets(allChapters);
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, allChapters.size());
        List<Chapter> paginatedChapters = allChapters.subList(start, end);
        
        Map<String, Object> result = new HashMap<>();
        result.put("chapters", paginatedChapters);
        result.put("totalResults", allChapters.size());
        result.put("facets", facets);
        result.put("page", page);
        result.put("size", size);
        
        return result;
    }

    /**
     * Search chapters by geographic location
     */
    public List<Chapter> searchByLocation(double latitude, double longitude, double radiusKm, int limit) {
        // For demo purposes, return chapters from nearby states
        // In production, you would use actual geographic coordinates
        List<Chapter> allChapters = chapterRepository.findAll();
        
        // Simple proximity simulation based on state
        Map<String, Double> stateDistances = getStateDistances(latitude, longitude);
        
        return allChapters.stream()
                .filter(chapter -> {
                    Double distance = stateDistances.get(chapter.getState());
                    return distance != null && distance <= radiusKm;
                })
                .sorted((c1, c2) -> {
                    Double d1 = stateDistances.get(c1.getState());
                    Double d2 = stateDistances.get(c2.getState());
                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return 1;
                    if (d2 == null) return -1;
                    return Double.compare(d1, d2);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Save search query for analytics
     */
    public void saveSearchQuery(String query, Long userId, Map<String, String> filters) {
        // Update search count
        searchQueryCounts.put(query, searchQueryCounts.getOrDefault(query, 0) + 1);
        
        // Save user search if userId provided
        if (userId != null) {
            userSavedSearches.computeIfAbsent(userId, k -> new ArrayList<>())
                    .add(Map.of(
                            "query", query,
                            "filters", filters != null ? filters : Map.of(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    /**
     * Get saved searches for a user
     */
    public List<Map<String, Object>> getSavedSearches(Long userId) {
        return userSavedSearches.getOrDefault(userId, new ArrayList<>());
    }

    // Helper methods
    private boolean matchesQuery(Chapter chapter, String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        
        String queryLower = query.toLowerCase();
        return (chapter.getName() != null && chapter.getName().toLowerCase().contains(queryLower)) ||
               (chapter.getUniversityName() != null && chapter.getUniversityName().toLowerCase().contains(queryLower)) ||
               (chapter.getCity() != null && chapter.getCity().toLowerCase().contains(queryLower)) ||
               (chapter.getState() != null && chapter.getState().toLowerCase().contains(queryLower)) ||
               (chapter.getDescription() != null && chapter.getDescription().toLowerCase().contains(queryLower));
    }

    private int getMemberCount(Chapter chapter) {
        return memberRepository.findByChapterId(chapter.getId()).size();
    }

    private int getHealthScore(Chapter chapter) {
        // Calculate health score based on various factors
        int memberCount = getMemberCount(chapter);
        boolean isActive = Boolean.TRUE.equals(chapter.getActive());
        
        int score = 0;
        if (isActive) score += 50;
        if (memberCount > 30) score += 30;
        else if (memberCount > 15) score += 20;
        else if (memberCount > 5) score += 10;
        
        // Add random factor for demo
        score += (int)(Math.random() * 20);
        
        return Math.min(100, score);
    }

    private List<Chapter> applySorting(List<Chapter> chapters, String sortBy, String query) {
        switch (sortBy) {
            case "name":
                return chapters.stream().sorted(Comparator.comparing(Chapter::getName, Comparator.nullsLast(String::compareToIgnoreCase))).collect(Collectors.toList());
            case "members":
                return chapters.stream().sorted((c1, c2) -> Integer.compare(getMemberCount(c2), getMemberCount(c1))).collect(Collectors.toList());
            case "health":
                return chapters.stream().sorted((c1, c2) -> Integer.compare(getHealthScore(c2), getHealthScore(c1))).collect(Collectors.toList());
            case "relevance":
            default:
                // Sort by relevance to query
                return chapters.stream()
                        .sorted((c1, c2) -> Double.compare(calculateRelevance(c2, query), calculateRelevance(c1, query)))
                        .collect(Collectors.toList());
        }
    }

    private double calculateRelevance(Chapter chapter, String query) {
        if (query == null || query.trim().isEmpty()) {
            return getMemberCount(chapter) + getHealthScore(chapter);
        }
        
        String queryLower = query.toLowerCase();
        double relevance = 0;
        
        if (chapter.getName() != null && chapter.getName().toLowerCase().contains(queryLower)) {
            relevance += 100;
            if (chapter.getName().toLowerCase().startsWith(queryLower)) {
                relevance += 50;
            }
        }
        
        if (chapter.getUniversityName() != null && chapter.getUniversityName().toLowerCase().contains(queryLower)) {
            relevance += 75;
        }
        
        if (chapter.getCity() != null && chapter.getCity().toLowerCase().contains(queryLower)) {
            relevance += 50;
        }
        
        if (chapter.getState() != null && chapter.getState().toLowerCase().contains(queryLower)) {
            relevance += 25;
        }
        
        // Boost active chapters
        if (Boolean.TRUE.equals(chapter.getActive())) {
            relevance += 25;
        }
        
        // Boost chapters with more members
        relevance += getMemberCount(chapter) * 2;
        
        return relevance;
    }

    private Map<String, Object> generateFacets(List<Chapter> chapters) {
        Map<String, Object> facets = new HashMap<>();
        
        // State facets
        Map<String, Long> stateFacets = chapters.stream()
                .collect(Collectors.groupingBy(
                        chapter -> chapter.getState() != null ? chapter.getState() : "Unknown",
                        Collectors.counting()
                ));
        facets.put("states", stateFacets);
        
        // Status facets
        Map<String, Long> statusFacets = chapters.stream()
                .collect(Collectors.groupingBy(
                        chapter -> Boolean.TRUE.equals(chapter.getActive()) ? "Active" : "Inactive",
                        Collectors.counting()
                ));
        facets.put("statuses", statusFacets);
        
        // Member count ranges
        Map<String, Long> memberRanges = new HashMap<>();
        chapters.forEach(chapter -> {
            int count = getMemberCount(chapter);
            String range;
            if (count == 0) range = "0";
            else if (count <= 10) range = "1-10";
            else if (count <= 25) range = "11-25";
            else if (count <= 50) range = "26-50";
            else range = "50+";
            
            memberRanges.put(range, memberRanges.getOrDefault(range, 0L) + 1);
        });
        facets.put("memberRanges", memberRanges);
        
        return facets;
    }

    private List<String> generateSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        
        if (query == null || query.trim().isEmpty()) {
            suggestions.addAll(Arrays.asList("Active chapters", "California", "Large chapters", "University", "Tech"));
        } else {
            // Add common search extensions
            suggestions.add(query + " university");
            suggestions.add(query + " active");
            suggestions.add(query + " chapters");
            
            // Add related terms
            if (query.toLowerCase().contains("tech")) {
                suggestions.addAll(Arrays.asList("Technology chapters", "Engineering", "Computer Science"));
            }
            if (query.toLowerCase().contains("california")) {
                suggestions.addAll(Arrays.asList("California universities", "Silicon Valley", "Los Angeles"));
            }
        }
        
        return suggestions.stream().distinct().limit(5).collect(Collectors.toList());
    }

    private double calculateSimilarity(Chapter target, Chapter other) {
        double similarity = 0;
        
        // State similarity
        if (Objects.equals(target.getState(), other.getState())) {
            similarity += 30;
        }
        
        // Member count similarity
        int targetMembers = getMemberCount(target);
        int otherMembers = getMemberCount(other);
        double memberSimilarity = 1.0 - Math.abs(targetMembers - otherMembers) / (double) Math.max(targetMembers, otherMembers);
        similarity += memberSimilarity * 25;
        
        // Status similarity
        if (Objects.equals(target.getActive(), other.getActive())) {
            similarity += 20;
        }
        
        // University name similarity (simple)
        if (target.getUniversityName() != null && other.getUniversityName() != null) {
            String[] targetWords = target.getUniversityName().toLowerCase().split("\\s+");
            String[] otherWords = other.getUniversityName().toLowerCase().split("\\s+");
            long commonWords = Arrays.stream(targetWords)
                    .filter(word -> Arrays.asList(otherWords).contains(word))
                    .count();
            similarity += (double) commonWords / Math.max(targetWords.length, otherWords.length) * 25;
        }
        
        return similarity;
    }

    private List<RecommendationDto> getLocationBasedRecommendations(Member member, List<Chapter> chapters) {
        List<RecommendationDto> recommendations = new ArrayList<>();
        
        // Get member's chapter state if available
        String memberState = null;
        if (member.getChapter() != null) {
            memberState = member.getChapter().getState();
        }
        
        if (memberState != null) {
            final String state = memberState;
            List<Chapter> stateChapters = chapters.stream()
                    .filter(chapter -> Objects.equals(chapter.getState(), state))
                    .filter(chapter -> !Objects.equals(chapter.getId(), member.getChapter().getId()))
                    .limit(3)
                    .collect(Collectors.toList());
            
            stateChapters.forEach(chapter -> {
                recommendations.add(new RecommendationDto(
                        chapter,
                        "LOCATION",
                        85.0,
                        "Same state as your chapter",
                        "This chapter is in " + state + ", same as your current chapter"
                ));
            });
        }
        
        return recommendations;
    }

    private List<RecommendationDto> getSizeBasedRecommendations(Member member, List<Chapter> chapters) {
        List<RecommendationDto> recommendations = new ArrayList<>();
        
        if (member.getChapter() != null) {
            int memberChapterSize = getMemberCount(member.getChapter());
            
            chapters.stream()
                    .filter(chapter -> !Objects.equals(chapter.getId(), member.getChapter().getId()))
                    .filter(chapter -> {
                        int chapterSize = getMemberCount(chapter);
                        return Math.abs(chapterSize - memberChapterSize) <= 10;
                    })
                    .limit(2)
                    .forEach(chapter -> {
                        recommendations.add(new RecommendationDto(
                                chapter,
                                "SIZE",
                                75.0,
                                "Similar size to your chapter",
                                "This chapter has " + getMemberCount(chapter) + " members, similar to your chapter"
                        ));
                    });
        }
        
        return recommendations;
    }

    private List<RecommendationDto> getActivityBasedRecommendations(Member member, List<Chapter> chapters) {
        List<RecommendationDto> recommendations = new ArrayList<>();
        
        // Recommend highly active chapters
        chapters.stream()
                .filter(chapter -> Boolean.TRUE.equals(chapter.getActive()))
                .filter(chapter -> getMemberCount(chapter) > 20)
                .filter(chapter -> member.getChapter() == null || !Objects.equals(chapter.getId(), member.getChapter().getId()))
                .sorted((c1, c2) -> Integer.compare(getHealthScore(c2), getHealthScore(c1)))
                .limit(2)
                .forEach(chapter -> {
                    recommendations.add(new RecommendationDto(
                            chapter,
                            "ACTIVITY",
                            90.0,
                            "Highly active chapter",
                            "This chapter has high engagement with " + getMemberCount(chapter) + " active members"
                    ));
                });
        
        return recommendations;
    }

    private List<RecommendationDto> getGeneralRecommendations(int limit) {
        List<RecommendationDto> recommendations = new ArrayList<>();
        List<Chapter> topChapters = chapterRepository.findAll().stream()
                .filter(chapter -> Boolean.TRUE.equals(chapter.getActive()))
                .sorted((c1, c2) -> Integer.compare(getMemberCount(c2), getMemberCount(c1)))
                .limit(limit)
                .collect(Collectors.toList());
        
        topChapters.forEach(chapter -> {
            recommendations.add(new RecommendationDto(
                    chapter,
                    "POPULAR",
                    80.0,
                    "Popular chapter",
                    "One of the most active chapters with " + getMemberCount(chapter) + " members"
            ));
        });
        
        return recommendations;
    }

    private List<Chapter> applyFilter(List<Chapter> chapters, String filterKey, String filterValue) {
        switch (filterKey) {
            case "state":
                return chapters.stream()
                        .filter(chapter -> Objects.equals(chapter.getState(), filterValue))
                        .collect(Collectors.toList());
            case "status":
                return chapters.stream()
                        .filter(chapter -> {
                            if ("Active".equals(filterValue)) {
                                return Boolean.TRUE.equals(chapter.getActive());
                            } else if ("Inactive".equals(filterValue)) {
                                return Boolean.FALSE.equals(chapter.getActive());
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
            default:
                return chapters;
        }
    }

    private Map<String, Double> getStateDistances(@SuppressWarnings("unused") double latitude, 
                                                  @SuppressWarnings("unused") double longitude) {
        // Simple mock distances for demo - in production use actual geographic calculation
        // TODO: Implement actual geographic distance calculation using latitude/longitude
        Map<String, Double> distances = new HashMap<>();
        distances.put("California", 10.0);
        distances.put("Nevada", 50.0);
        distances.put("Arizona", 75.0);
        distances.put("Texas", 200.0);
        distances.put("New York", 500.0);
        distances.put("Florida", 400.0);
        // Add more states as needed
        return distances;
    }
}