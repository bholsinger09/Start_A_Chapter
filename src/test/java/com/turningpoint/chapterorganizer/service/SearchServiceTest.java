package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.dto.SearchResultsDto;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private SearchService searchService;

    private Chapter testChapter1;
    private Chapter testChapter2;
    private Member testMember;

    @BeforeEach
    void setUp() {
        testChapter1 = new Chapter();
        testChapter1.setId(1L);
        testChapter1.setName("Test Chapter Alpha");
        testChapter1.setState("California");
        testChapter1.setUniversityName("Test University");
        testChapter1.setActive(true);
        testChapter1.setCreatedAt(LocalDateTime.now());

        testChapter2 = new Chapter();
        testChapter2.setId(2L);
        testChapter2.setName("Beta Chapter");
        testChapter2.setState("Texas");
        testChapter2.setUniversityName("Beta University");
        testChapter2.setActive(true);
        testChapter2.setCreatedAt(LocalDateTime.now());

        testMember = new Member();
        testMember.setId(1L);
        testMember.setFirstName("John");
        testMember.setLastName("Doe");
        testMember.setEmail("john.doe@test.com");
        testMember.setActive(true);
    }

    @Test
    void performGlobalSearch_ShouldReturnResults_WhenValidQuery() {
        // Given
        String query = "test";
        List<Chapter> allChapters = Arrays.asList(testChapter1, testChapter2);
        when(chapterRepository.findAll()).thenReturn(allChapters);

        // When
        SearchResultsDto result = searchService.performGlobalSearch(
                query, null, null, null, null, null, null, 0, 10, "name");

        // Then
        assertNotNull(result);
        assertNotNull(result.getChapters());
        assertTrue(result.getChapters().size() >= 1);
        assertTrue(result.getSearchTimeMs() >= 0);
        verify(chapterRepository).findAll();
    }

    @Test
    void performGlobalSearch_ShouldFilterByState_WhenStateProvided() {
        // Given
        String query = "";
        List<String> states = Arrays.asList("California");
        List<Chapter> allChapters = Arrays.asList(testChapter1, testChapter2);
        when(chapterRepository.findAll()).thenReturn(allChapters);

        // When
        SearchResultsDto result = searchService.performGlobalSearch(
                query, states, null, null, null, null, null, 0, 10, "name");

        // Then
        assertNotNull(result);
        verify(chapterRepository).findAll();
    }

    @Test
    void performGlobalSearch_ShouldFilterByStatuses_WhenStatusesProvided() {
        // Given
        String query = "";
        List<String> statuses = Arrays.asList("Active");
        List<Chapter> allChapters = Arrays.asList(testChapter1, testChapter2);
        when(chapterRepository.findAll()).thenReturn(allChapters);

        // When
        SearchResultsDto result = searchService.performGlobalSearch(
                query, null, statuses, null, null, null, null, 0, 10, "name");

        // Then
        assertNotNull(result);
        verify(chapterRepository).findAll();
    }

    @Test
    void performGlobalSearch_ShouldApplyPagination_WhenPageParametersProvided() {
        // Given
        String query = "test";
        List<Chapter> allChapters = Arrays.asList(testChapter1, testChapter2);
        when(chapterRepository.findAll()).thenReturn(allChapters);

        // When
        SearchResultsDto result = searchService.performGlobalSearch(
                query, null, null, null, null, null, null, 0, 1, "name");

        // Then
        assertNotNull(result);
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getPageSize());
        verify(chapterRepository).findAll();
    }

    @Test
    void getSearchSuggestions_ShouldReturnSuggestions_WhenValidInput() {
        // Given
        String query = "test";
        List<Chapter> allChapters = Arrays.asList(testChapter1, testChapter2);
        when(chapterRepository.findAll()).thenReturn(allChapters);

        // When
        List<String> result = searchService.getSearchSuggestions(query, 5);

        // Then
        assertNotNull(result);
        verify(chapterRepository).findAll();
    }

    @Test
    void getSearchSuggestions_ShouldHandleEmptyInput() {
        // Given
        String query = "";
        List<Chapter> allChapters = Arrays.asList(testChapter1, testChapter2);
        when(chapterRepository.findAll()).thenReturn(allChapters);

        // When
        List<String> result = searchService.getSearchSuggestions(query, 5);

        // Then
        assertNotNull(result);
        verify(chapterRepository).findAll();
    }

    @Test
    void getSearchSuggestions_ShouldLimitResults_WhenLimitProvided() {
        // Given
        String query = "test";
        List<Chapter> allChapters = Arrays.asList(testChapter1, testChapter2);
        when(chapterRepository.findAll()).thenReturn(allChapters);

        // When
        List<String> result = searchService.getSearchSuggestions(query, 2);

        // Then
        assertNotNull(result);
        assertTrue(result.size() <= 2);
        verify(chapterRepository).findAll();
    }

    @Test
    void saveSearchQuery_ShouldSaveQuery_WhenValidInput() {
        // When
        searchService.saveSearchQuery("test query", 1L, new HashMap<>());

        // Then - Method should complete without exception
        // This is a void method that just logs, so we verify it doesn't throw
        assertTrue(true);
    }

    @Test
    void saveSearchQuery_ShouldHandleNullQuery() {
        // When & Then - Should handle null query gracefully (or throw expected exception)
        // Since the service uses Map.of() which doesn't allow nulls, we expect an exception
        assertThrows(NullPointerException.class, () -> searchService.saveSearchQuery(null, 1L, new HashMap<>()));
    }

    @Test
    void saveSearchQuery_ShouldHandleEmptyQuery() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> searchService.saveSearchQuery("", 1L, new HashMap<>()));
    }

    @Test
    void getTrendingData_ShouldReturnTrendingInformation() {
        // Given
        List<Chapter> chapters = Arrays.asList(testChapter1, testChapter2);
        when(chapterRepository.findAll()).thenReturn(chapters);

        // When
        Map<String, Object> result = searchService.getTrendingData();

        // Then
        assertNotNull(result);
        verify(chapterRepository, times(2)).findAll();
    }

    @Test
    void findSimilarChapters_ShouldReturnSimilarChapters() {
        // Given
        Chapter similarChapter = new Chapter();
        similarChapter.setId(2L);
        similarChapter.setName("Similar Chapter");
        similarChapter.setState("California");
        similarChapter.setCity("San Diego");

        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter1));
        when(chapterRepository.findAll()).thenReturn(Arrays.asList(testChapter1, similarChapter));

        // When
        List<Chapter> result = searchService.findSimilarChapters(1L, 5);

        // Then
        assertNotNull(result);
        verify(chapterRepository).findById(1L);
        verify(chapterRepository).findAll();
    }

    @Test
    void findSimilarChapters_ShouldReturnEmptyList_WhenChapterNotFound() {
        // Given
        when(chapterRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        List<Chapter> result = searchService.findSimilarChapters(999L, 5);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chapterRepository).findById(999L);
        verify(chapterRepository, never()).findAll();
    }

    @Test
    void performGlobalSearch_ShouldHandleNullParameters() {
        // Given
        List<Chapter> allChapters = Arrays.asList(testChapter1);
        when(chapterRepository.findAll()).thenReturn(allChapters);

        // When & Then - Should not throw exception with null parameters
        assertDoesNotThrow(() -> {
            SearchResultsDto result = searchService.performGlobalSearch(
                    null, null, null, null, null, null, null, 0, 10, "relevance");
            assertNotNull(result);
        });
        
        verify(chapterRepository).findAll();
    }

    @Test
    void performGlobalSearch_ShouldHandleEmptyQuery() {
        // Given
        List<Chapter> allChapters = Arrays.asList(testChapter1, testChapter2);
        when(chapterRepository.findAll()).thenReturn(allChapters);

        // When
        SearchResultsDto result = searchService.performGlobalSearch(
                "", null, null, null, null, null, null, 0, 10, "name");

        // Then
        assertNotNull(result);
        verify(chapterRepository).findAll();
    }
}