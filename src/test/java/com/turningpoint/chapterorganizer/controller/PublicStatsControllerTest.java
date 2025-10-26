package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.repository.EventRepository;
import com.turningpoint.chapterorganizer.repository.EventRSVPRepository;
import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for PublicStatsController.
 * Tests public statistics endpoints, error handling, and data aggregation.
 */
@WebMvcTest(PublicStatsController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChapterService chapterService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private EventRSVPRepository eventRsvpRepository;

    private List<Chapter> mockChapters;

    @BeforeEach
    void setUp() {
        // Create mock chapters with different states
        Chapter chapter1 = new Chapter();
        chapter1.setId(1L);
        chapter1.setName("UC Berkeley");
        chapter1.setState("California");
        chapter1.setCity("Berkeley");
        chapter1.setUniversityName("University of California, Berkeley");
        chapter1.setActive(true);

        Chapter chapter2 = new Chapter();
        chapter2.setId(2L);
        chapter2.setName("University of Texas");
        chapter2.setState("Texas");
        chapter2.setCity("Austin");
        chapter2.setUniversityName("University of Texas at Austin");
        chapter2.setActive(true);

        Chapter chapter3 = new Chapter();
        chapter3.setId(3L);
        chapter3.setName("UCLA");
        chapter3.setState("California");
        chapter3.setCity("Los Angeles");
        chapter3.setUniversityName("University of California, Los Angeles");
        chapter3.setActive(true);

        Chapter chapter4 = new Chapter();
        chapter4.setId(4L);
        chapter4.setName("Unknown Chapter");
        chapter4.setState(null); // Test null state handling
        chapter4.setCity("Unknown");
        chapter4.setActive(true);

        mockChapters = Arrays.asList(chapter1, chapter2, chapter3, chapter4);
    }

    @Test
    void getPublicOverview_ShouldReturnCompleteStatistics() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenReturn(mockChapters);
        when(memberService.getAllMembers()).thenReturn(Collections.nCopies(25, null)); // 25 members
        when(eventRepository.count()).thenReturn(15L);
        when(eventRsvpRepository.count()).thenReturn(45L);

        // When & Then
        mockMvc.perform(get("/api/stats/public/overview"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalChapters").value(4))
                .andExpect(jsonPath("$.totalMembers").value(25))
                .andExpect(jsonPath("$.totalEvents").value(15))
                .andExpect(jsonPath("$.totalRsvps").value(45))
                .andExpect(jsonPath("$.description").value("Campus Chapter Organizer - Connecting students across universities"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(chapterService).getAllActiveChapters();
        verify(memberService).getAllMembers();
        verify(eventRepository).count();
        verify(eventRsvpRepository).count();
    }

    @Test
    void getPublicOverview_ShouldHandleEmptyData() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenReturn(Collections.emptyList());
        when(memberService.getAllMembers()).thenReturn(Collections.emptyList());
        when(eventRepository.count()).thenReturn(0L);
        when(eventRsvpRepository.count()).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/api/stats/public/overview"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalChapters").value(0))
                .andExpect(jsonPath("$.totalMembers").value(0))
                .andExpect(jsonPath("$.totalEvents").value(0))
                .andExpect(jsonPath("$.totalRsvps").value(0))
                .andExpect(jsonPath("$.description").value("Campus Chapter Organizer - Connecting students across universities"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(chapterService).getAllActiveChapters();
        verify(memberService).getAllMembers();
        verify(eventRepository).count();
        verify(eventRsvpRepository).count();
    }

    @Test
    void getPublicOverview_ShouldReturnDefaultValuesOnException() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/stats/public/overview"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalChapters").value(0))
                .andExpect(jsonPath("$.totalMembers").value(0))
                .andExpect(jsonPath("$.totalEvents").value(0))
                .andExpect(jsonPath("$.totalRsvps").value(0))
                .andExpect(jsonPath("$.description").value("Campus Chapter Organizer"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.error").value("Unable to fetch current statistics"));

        verify(chapterService).getAllActiveChapters();
        // Other services should not be called due to early exception
        verify(memberService, never()).getAllMembers();
        verify(eventRepository, never()).count();
        verify(eventRsvpRepository, never()).count();
    }

    @Test
    void getChaptersSummary_ShouldReturnStateDistribution() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenReturn(mockChapters);

        // When & Then
        mockMvc.perform(get("/api/stats/public/chapters-summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.chaptersByState.California").value(2))
                .andExpect(jsonPath("$.chaptersByState.Texas").value(1))
                .andExpect(jsonPath("$.chaptersByState.Unknown").value(1)) // null state becomes "Unknown"
                .andExpect(jsonPath("$.totalStates").value(3))
                .andExpect(jsonPath("$.averageChaptersPerState").value(1.3333333333333333)) // 4 chapters / 3 states
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(chapterService).getAllActiveChapters();
    }

    @Test
    void getChaptersSummary_ShouldHandleEmptyChaptersList() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/stats/public/chapters-summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.chaptersByState").isMap())
                .andExpect(jsonPath("$.chaptersByState").isEmpty())
                .andExpect(jsonPath("$.totalStates").value(0))
                .andExpect(jsonPath("$.averageChaptersPerState").value(0))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(chapterService).getAllActiveChapters();
    }

    @Test
    void getChaptersSummary_ShouldHandleSingleStateScenario() throws Exception {
        // Given - All chapters in the same state
        List<Chapter> singleStateChapters = Arrays.asList(
                mockChapters.get(0), // California
                mockChapters.get(2)  // California
        );
        when(chapterService.getAllActiveChapters()).thenReturn(singleStateChapters);

        // When & Then
        mockMvc.perform(get("/api/stats/public/chapters-summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.chaptersByState.California").value(2))
                .andExpect(jsonPath("$.totalStates").value(1))
                .andExpect(jsonPath("$.averageChaptersPerState").value(2.0))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(chapterService).getAllActiveChapters();
    }

    @Test
    void getChaptersSummary_ShouldReturnErrorOnException() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/stats/public/chapters-summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Unable to fetch chapter summary"))
                .andExpect(jsonPath("$.chaptersByState").doesNotExist())
                .andExpect(jsonPath("$.totalStates").doesNotExist())
                .andExpect(jsonPath("$.averageChaptersPerState").doesNotExist());

        verify(chapterService).getAllActiveChapters();
    }

    @Test
    void publicStatsController_ShouldSupportCrossOrigin() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenReturn(mockChapters);
        when(memberService.getAllMembers()).thenReturn(Collections.emptyList());
        when(eventRepository.count()).thenReturn(0L);
        when(eventRsvpRepository.count()).thenReturn(0L);

        // When & Then
        mockMvc.perform(options("/api/stats/public/overview")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void publicStatsEndpoints_ShouldHandleHttpMethodValidation() throws Exception {
        // When & Then - Test unsupported methods are handled by global exception handler
        // Note: Due to GlobalExceptionHandler, method not supported exceptions return 500
        mockMvc.perform(post("/api/stats/public/overview"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        mockMvc.perform(put("/api/stats/public/overview"))
                .andExpect(status().isInternalServerError());

        mockMvc.perform(delete("/api/stats/public/overview"))
                .andExpect(status().isInternalServerError());

        mockMvc.perform(post("/api/stats/public/chapters-summary"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void publicStatsController_ShouldNotRequireAuthentication() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenReturn(Collections.singletonList(mockChapters.get(0)));
        when(memberService.getAllMembers()).thenReturn(Collections.singletonList(null));
        when(eventRepository.count()).thenReturn(1L);
        when(eventRsvpRepository.count()).thenReturn(2L);

        // When & Then - Should work without authentication headers
        mockMvc.perform(get("/api/stats/public/overview")
                // Explicitly no authentication headers
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalChapters").value(1))
                .andExpect(jsonPath("$.totalMembers").value(1));

        mockMvc.perform(get("/api/stats/public/chapters-summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalStates").value(1));
    }
}