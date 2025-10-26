package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Event;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import com.turningpoint.chapterorganizer.repository.EventRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for DataController.
 * Tests data population, statistics retrieval, and error handling.
 */
@WebMvcTest(DataController.class)
@AutoConfigureMockMvc(addFilters = false)
class DataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChapterRepository chapterRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private EventRepository eventRepository;

    private List<Chapter> mockChapters;

    @BeforeEach
    void setUp() {
        // Create mock chapters
        Chapter chapter1 = new Chapter();
        chapter1.setId(1L);
        chapter1.setName("UC Berkeley");
        chapter1.setState("California");
        chapter1.setCity("Berkeley");
        chapter1.setUniversityName("University of California, Berkeley");

        Chapter chapter2 = new Chapter();
        chapter2.setId(2L);
        chapter2.setName("University of Texas");
        chapter2.setState("Texas");
        chapter2.setCity("Austin");
        chapter2.setUniversityName("University of Texas at Austin");

        mockChapters = Arrays.asList(chapter1, chapter2);
    }

    @Test
    void populateAllStatesData_ShouldSuccessfullyPopulateData() throws Exception {
        // Given
        when(chapterRepository.save(any(Chapter.class))).thenAnswer(invocation -> {
            Chapter chapter = invocation.getArgument(0);
            chapter.setId(1L);
            return chapter;
        });
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setId(1L);
            return member;
        });
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            event.setId(1L);
            return event;
        });
        
        when(chapterRepository.count()).thenReturn(50L);
        when(memberRepository.count()).thenReturn(150L);
        when(eventRepository.count()).thenReturn(75L);

        // When & Then
        mockMvc.perform(post("/api/admin/populate-all-states"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(
                    "Successfully populated data for all 50 states! Created 50 chapters, 150 members, and 75 events."
                ));

        // Verify all repositories were called for cleanup
        verify(eventRepository).deleteAll();
        verify(memberRepository).deleteAll();
        verify(chapterRepository).deleteAll();

        // Verify data was created (at least some chapters, members, events)
        verify(chapterRepository, atLeastOnce()).save(any(Chapter.class));
        verify(memberRepository, atLeastOnce()).save(any(Member.class));
        verify(eventRepository, atLeastOnce()).save(any(Event.class));
        
        // Verify counts were retrieved
        verify(chapterRepository).count();
        verify(memberRepository).count();
        verify(eventRepository).count();
    }

    @Test
    void populateAllStatesData_ShouldHandleRepositoryException() throws Exception {
        // Given
        doThrow(new RuntimeException("Database connection failed"))
                .when(chapterRepository).deleteAll();

        // When & Then
        mockMvc.perform(post("/api/admin/populate-all-states"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Error populating data: Database connection failed"));

        // Verify the sequence of calls - all deleteAll() methods are called before exception handling
        verify(eventRepository).deleteAll();
        verify(memberRepository).deleteAll();
        verify(chapterRepository).deleteAll();
    }

    @Test
    void getDataStats_ShouldReturnCurrentStatistics() throws Exception {
        // Given
        when(chapterRepository.count()).thenReturn(25L);
        when(memberRepository.count()).thenReturn(100L);
        when(eventRepository.count()).thenReturn(50L);
        when(chapterRepository.findAll()).thenReturn(mockChapters);

        // When & Then
        mockMvc.perform(get("/api/admin/data-stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(
                    "Current data: 25 chapters, 100 members, 50 events across 2 states: California, Texas"
                ));

        verify(chapterRepository).count();
        verify(memberRepository).count();
        verify(eventRepository).count();
        verify(chapterRepository).findAll();
    }

    @Test
    void getDataStats_ShouldHandleEmptyData() throws Exception {
        // Given
        when(chapterRepository.count()).thenReturn(0L);
        when(memberRepository.count()).thenReturn(0L);
        when(eventRepository.count()).thenReturn(0L);
        when(chapterRepository.findAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/admin/data-stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(
                    "Current data: 0 chapters, 0 members, 0 events across 0 states: "
                ));

        verify(chapterRepository).count();
        verify(memberRepository).count();
        verify(eventRepository).count();
        verify(chapterRepository).findAll();
    }

    @Test
    void dataController_ShouldSupportCrossOrigin() throws Exception {
        // When & Then
        mockMvc.perform(options("/api/admin/data-stats")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void populateAllStatesData_ShouldHandleOnlyHttpPost() throws Exception {
        // When & Then - Other methods should return 500 status
        mockMvc.perform(get("/api/admin/populate-all-states"))
                .andExpect(status().is5xxServerError());

        mockMvc.perform(put("/api/admin/populate-all-states"))
                .andExpect(status().is5xxServerError());

        mockMvc.perform(delete("/api/admin/populate-all-states"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getDataStats_ShouldHandleOnlyHttpGet() throws Exception {
        // When & Then - Other methods should return 500 status
        mockMvc.perform(post("/api/admin/data-stats"))
                .andExpect(status().is5xxServerError());

        mockMvc.perform(put("/api/admin/data-stats"))
                .andExpect(status().is5xxServerError());

        mockMvc.perform(delete("/api/admin/data-stats"))
                .andExpect(status().is5xxServerError());
    }
}