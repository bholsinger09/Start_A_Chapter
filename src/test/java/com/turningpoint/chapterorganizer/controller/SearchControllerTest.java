package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.dto.SearchResultsDto;
import com.turningpoint.chapterorganizer.dto.RecommendationDto;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for SearchController.
 * Tests search functionality, recommendations, query validation, and result handling.
 */
@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    private SearchResultsDto mockSearchResults;
    private List<RecommendationDto> mockRecommendations;
    private List<Chapter> mockChapters;
    private Map<String, Object> mockTrendingData;
    private Map<String, Object> mockFacetedResults;

    @BeforeEach
    void setUp() {
        // Create mock Chapter
        Chapter mockChapter = new Chapter();
        mockChapter.setId(1L);
        mockChapter.setName("Test Chapter");
        mockChapter.setState("California");
        mockChapter.setCity("San Francisco");

        // Create mock chapters list
        mockChapters = Arrays.asList(mockChapter);

        // Create mock search results
        mockSearchResults = new SearchResultsDto(
            mockChapters,
            100L,
            5,
            0,
            20,
            new HashMap<>(),
            Arrays.asList("suggestion1", "suggestion2"),
            150L
        );

        // Create mock recommendations
        RecommendationDto mockRecommendation = new RecommendationDto(
            mockChapter,
            "location",
            0.85,
            "Nearby chapter",
            "This chapter is in your area"
        );
        mockRecommendations = Arrays.asList(mockRecommendation);

        // Create mock trending data
        mockTrendingData = new HashMap<>();
        mockTrendingData.put("trendingTerms", Arrays.asList("wellness", "community"));
        mockTrendingData.put("popularChapters", mockChapters);

        // Create mock faceted results
        mockFacetedResults = new HashMap<>();
        mockFacetedResults.put("results", mockChapters);
        mockFacetedResults.put("facets", new HashMap<>());
    }

    @Test
    void globalSearch_ShouldReturnResults_WhenValidParameters() throws Exception {
        // Given
        when(searchService.performGlobalSearch(
            eq("test query"),
            eq(Arrays.asList("California")),
            eq(Arrays.asList("ACTIVE")),
            eq(10),
            eq(100),
            eq(70),
            eq(95),
            eq(0),
            eq(20),
            eq("relevance")
        )).thenReturn(mockSearchResults);

        // When & Then
        mockMvc.perform(get("/api/search/global")
                .param("query", "test query")
                .param("states", "California")
                .param("statuses", "ACTIVE")
                .param("minMembers", "10")
                .param("maxMembers", "100")
                .param("minHealthScore", "70")
                .param("maxHealthScore", "95")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "relevance"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalResults").value(100))
                .andExpect(jsonPath("$.chapters[0].name").value("Test Chapter"));

        verify(searchService).performGlobalSearch(
            eq("test query"),
            eq(Arrays.asList("California")),
            eq(Arrays.asList("ACTIVE")),
            eq(10),
            eq(100),
            eq(70),
            eq(95),
            eq(0),
            eq(20),
            eq("relevance")
        );
    }

    @Test
    void globalSearch_ShouldUseDefaults_WhenOptionalParametersNotProvided() throws Exception {
        // Given
        when(searchService.performGlobalSearch(
            eq(""),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            eq(0),
            eq(20),
            eq("relevance")
        )).thenReturn(mockSearchResults);

        // When & Then
        mockMvc.perform(get("/api/search/global"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(searchService).performGlobalSearch(
            eq(""),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            eq(0),
            eq(20),
            eq("relevance")
        );
    }

    @Test
    void globalSearch_ShouldHandleMultipleStatesAndStatuses() throws Exception {
        // Given
        when(searchService.performGlobalSearch(
            anyString(),
            eq(Arrays.asList("California", "Texas")),
            eq(Arrays.asList("ACTIVE", "PENDING")),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            anyInt(),
            anyInt(),
            anyString()
        )).thenReturn(mockSearchResults);

        // When & Then
        mockMvc.perform(get("/api/search/global")
                .param("states", "California", "Texas")
                .param("statuses", "ACTIVE", "PENDING"))
                .andExpect(status().isOk());

        verify(searchService).performGlobalSearch(
            anyString(),
            eq(Arrays.asList("California", "Texas")),
            eq(Arrays.asList("ACTIVE", "PENDING")),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            anyInt(),
            anyInt(),
            anyString()
        );
    }

    @Test
    void getPersonalizedRecommendations_ShouldReturnRecommendations_WhenValidMemberId() throws Exception {
        // Given
        when(searchService.getPersonalizedRecommendations(1L, 10))
                .thenReturn(mockRecommendations);

        // When & Then
        mockMvc.perform(get("/api/search/recommendations/1")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].recommendationType").value("location"))
                .andExpect(jsonPath("$[0].score").value(0.85))
                .andExpect(jsonPath("$[0].reason").value("Nearby chapter"));

        verify(searchService).getPersonalizedRecommendations(1L, 10);
    }

    @Test
    void getPersonalizedRecommendations_ShouldUseDefaultLimit_WhenLimitNotProvided() throws Exception {
        // Given
        when(searchService.getPersonalizedRecommendations(1L, 10))
                .thenReturn(mockRecommendations);

        // When & Then
        mockMvc.perform(get("/api/search/recommendations/1"))
                .andExpect(status().isOk());

        verify(searchService).getPersonalizedRecommendations(1L, 10);
    }

    @Test
    void getGeneralRecommendations_ShouldReturnRecommendations_WhenNoMemberId() throws Exception {
        // Given
        when(searchService.getPersonalizedRecommendations(null, 5))
                .thenReturn(mockRecommendations);

        // When & Then
        mockMvc.perform(get("/api/search/recommendations")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].chapter.name").value("Test Chapter"));

        verify(searchService).getPersonalizedRecommendations(null, 5);
    }

    @Test
    void findSimilarChapters_ShouldReturnSimilarChapters_WhenValidChapterId() throws Exception {
        // Given
        when(searchService.findSimilarChapters(1L, 5))
                .thenReturn(mockChapters);

        // When & Then
        mockMvc.perform(get("/api/search/similar-chapters/1")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Chapter"))
                .andExpect(jsonPath("$[0].state").value("California"));

        verify(searchService).findSimilarChapters(1L, 5);
    }

    @Test
    void findSimilarChapters_ShouldUseDefaultLimit_WhenLimitNotProvided() throws Exception {
        // Given
        when(searchService.findSimilarChapters(1L, 5))
                .thenReturn(mockChapters);

        // When & Then
        mockMvc.perform(get("/api/search/similar-chapters/1"))
                .andExpect(status().isOk());

        verify(searchService).findSimilarChapters(1L, 5);
    }

    @Test
    void getSearchSuggestions_ShouldReturnSuggestions_WhenValidPartialQuery() throws Exception {
        // Given
        List<String> suggestions = Arrays.asList("test chapter", "test community", "test group");
        when(searchService.getSearchSuggestions("test", 10))
                .thenReturn(suggestions);

        // When & Then
        mockMvc.perform(get("/api/search/suggestions")
                .param("partial", "test")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("test chapter"))
                .andExpect(jsonPath("$[1]").value("test community"))
                .andExpect(jsonPath("$[2]").value("test group"));

        verify(searchService).getSearchSuggestions("test", 10);
    }

    @Test
    void getSearchSuggestions_ShouldUseDefaultLimit_WhenLimitNotProvided() throws Exception {
        // Given
        when(searchService.getSearchSuggestions("test", 10))
                .thenReturn(Arrays.asList("suggestion"));

        // When & Then
        mockMvc.perform(get("/api/search/suggestions")
                .param("partial", "test"))
                .andExpect(status().isOk());

        verify(searchService).getSearchSuggestions("test", 10);
    }

    @Test
    void getTrendingData_ShouldReturnTrendingData() throws Exception {
        // Given
        when(searchService.getTrendingData()).thenReturn(mockTrendingData);

        // When & Then
        mockMvc.perform(get("/api/search/trending"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.trendingTerms[0]").value("wellness"))
                .andExpect(jsonPath("$.trendingTerms[1]").value("community"));

        verify(searchService).getTrendingData();
    }

    @Test
    void facetedSearch_ShouldReturnFacetedResults_WhenValidParameters() throws Exception {
        // Given
        Map<String, String> filters = new HashMap<>();
        filters.put("state", "California");
        when(searchService.performFacetedSearch("wellness", filters, 0, 20))
                .thenReturn(mockFacetedResults);

        // When & Then
        mockMvc.perform(get("/api/search/faceted")
                .param("query", "wellness")
                .param("filters[state]", "California")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(searchService).performFacetedSearch(eq("wellness"), any(), eq(0), eq(20));
    }

    @Test
    void facetedSearch_ShouldUseDefaults_WhenOptionalParametersNotProvided() throws Exception {
        // Given
        when(searchService.performFacetedSearch(isNull(), any(), eq(0), eq(20)))
                .thenReturn(mockFacetedResults);

        // When & Then
        mockMvc.perform(get("/api/search/faceted"))
                .andExpect(status().isOk());

        verify(searchService).performFacetedSearch(isNull(), any(), eq(0), eq(20));
    }

    @Test
    void searchByLocation_ShouldReturnNearbyChapters_WhenValidCoordinates() throws Exception {
        // Given
        when(searchService.searchByLocation(37.7749, -122.4194, 50.0, 20))
                .thenReturn(mockChapters);

        // When & Then
        mockMvc.perform(get("/api/search/geographic")
                .param("latitude", "37.7749")
                .param("longitude", "-122.4194")
                .param("radiusKm", "50.0")
                .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Chapter"));

        verify(searchService).searchByLocation(37.7749, -122.4194, 50.0, 20);
    }

    @Test
    void searchByLocation_ShouldUseDefaults_WhenOptionalParametersNotProvided() throws Exception {
        // Given
        when(searchService.searchByLocation(37.7749, -122.4194, 50.0, 20))
                .thenReturn(mockChapters);

        // When & Then
        mockMvc.perform(get("/api/search/geographic")
                .param("latitude", "37.7749")
                .param("longitude", "-122.4194"))
                .andExpect(status().isOk());

        verify(searchService).searchByLocation(37.7749, -122.4194, 50.0, 20);
    }

    @Test
    void saveSearchQuery_ShouldSaveQuery_WhenValidParameters() throws Exception {
        // Given
        doNothing().when(searchService).saveSearchQuery(eq("wellness"), eq(1L), any());

        // When & Then
        mockMvc.perform(post("/api/search/save-query")
                .param("query", "wellness")
                .param("userId", "1")
                .param("filters[state]", "California"))
                .andExpect(status().isOk())
                .andExpect(content().string("Search query saved successfully"));

        verify(searchService).saveSearchQuery(eq("wellness"), eq(1L), any());
    }

    @Test
    void saveSearchQuery_ShouldSaveQuery_WhenUserIdNotProvided() throws Exception {
        // Given
        doNothing().when(searchService).saveSearchQuery(eq("wellness"), isNull(), any());

        // When & Then
        mockMvc.perform(post("/api/search/save-query")
                .param("query", "wellness"))
                .andExpect(status().isOk())
                .andExpect(content().string("Search query saved successfully"));

        verify(searchService).saveSearchQuery(eq("wellness"), isNull(), any());
    }

    @Test
    void getSavedSearches_ShouldReturnSavedSearches_WhenValidUserId() throws Exception {
        // Given
        List<Map<String, Object>> savedSearches = Arrays.asList(
            Map.of("query", "wellness", "timestamp", "2023-10-26"),
            Map.of("query", "community", "timestamp", "2023-10-25")
        );
        when(searchService.getSavedSearches(1L)).thenReturn(savedSearches);

        // When & Then
        mockMvc.perform(get("/api/search/saved/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].query").value("wellness"))
                .andExpect(jsonPath("$[1].query").value("community"));

        verify(searchService).getSavedSearches(1L);
    }

    @Test
    void searchController_ShouldHandleInvalidCoordinates_InGeographicSearch() throws Exception {
        // When & Then - Testing with invalid latitude
        mockMvc.perform(get("/api/search/geographic")
                .param("latitude", "invalid")
                .param("longitude", "-122.4194"))
                .andExpect(status().is5xxServerError());

        verify(searchService, never()).searchByLocation(anyDouble(), anyDouble(), anyDouble(), anyInt());
    }

    @Test
    void searchController_ShouldRequirePartialParameter_InSuggestions() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/search/suggestions"))
                .andExpect(status().is5xxServerError());

        verify(searchService, never()).getSearchSuggestions(anyString(), anyInt());
    }

    @Test
    void searchController_ShouldRequireQueryParameter_InSaveQuery() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/search/save-query"))
                .andExpect(status().is5xxServerError());

        verify(searchService, never()).saveSearchQuery(anyString(), any(), any());
    }

    @Test
    void searchController_ShouldHandleLargePageSizes() throws Exception {
        // Given
        when(searchService.performGlobalSearch(
            anyString(), any(), any(), any(), any(), any(), any(), eq(0), eq(1000), anyString()
        )).thenReturn(mockSearchResults);

        // When & Then
        mockMvc.perform(get("/api/search/global")
                .param("page", "0")
                .param("size", "1000"))
                .andExpect(status().isOk());

        verify(searchService).performGlobalSearch(
            anyString(), any(), any(), any(), any(), any(), any(), eq(0), eq(1000), anyString()
        );
    }

    @Test
    void searchController_ShouldHandleNegativePageNumbers() throws Exception {
        // Given
        when(searchService.performGlobalSearch(
            anyString(), any(), any(), any(), any(), any(), any(), eq(-1), anyInt(), anyString()
        )).thenReturn(mockSearchResults);

        // When & Then
        mockMvc.perform(get("/api/search/global")
                .param("page", "-1"))
                .andExpect(status().isOk());

        verify(searchService).performGlobalSearch(
            anyString(), any(), any(), any(), any(), any(), any(), eq(-1), anyInt(), anyString()
        );
    }

    @Test
    void searchController_ShouldSupportCrossOrigin() throws Exception {
        // When & Then
        mockMvc.perform(options("/api/search/global")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}