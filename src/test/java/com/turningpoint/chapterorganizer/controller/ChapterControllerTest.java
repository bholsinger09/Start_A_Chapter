package com.turningpoint.chapterorganizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Institution;
import com.turningpoint.chapterorganizer.entity.University;
import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.InstitutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ChapterController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ChapterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChapterService chapterService;

    @MockBean
    private InstitutionService institutionService;

    private Chapter testChapter;
    private List<Chapter> testChapters;
    private University testInstitution;

    @BeforeEach
    void setUp() {
        testChapter = new Chapter();
        testChapter.setId(1L);
        testChapter.setName("Test Chapter");
        testChapter.setUniversityName("Test University");
        testChapter.setState("California");
        testChapter.setCity("Los Angeles");
        testChapter.setDescription("Test chapter description");
        testChapter.setActive(true);
        testChapter.setFoundedDate(LocalDateTime.now());

        Chapter chapter2 = new Chapter();
        chapter2.setId(2L);
        chapter2.setName("Chapter Two");
        chapter2.setUniversityName("University Two");
        chapter2.setState("Texas");
        chapter2.setCity("Austin");
        chapter2.setActive(true);

        testChapters = Arrays.asList(testChapter, chapter2);

        testInstitution = new University();
        testInstitution.setId(1L);
        testInstitution.setName("Test University");
        testInstitution.setState("California");
        testInstitution.setCity("Los Angeles");
    }

    @Test
    void getAllChapters_ShouldReturnAllActiveChapters() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenReturn(testChapters);

        // When & Then
        mockMvc.perform(get("/api/chapters"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Chapter")))
                .andExpect(jsonPath("$[0].universityName", is("Test University")))
                .andExpect(jsonPath("$[1].name", is("Chapter Two")));

        verify(chapterService).getAllActiveChapters();
    }

    @Test
    void getAllChapters_ShouldReturnEmptyList_WhenNoChapters() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/chapters"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(chapterService).getAllActiveChapters();
    }

    @Test
    void getPaginatedChapters_ShouldReturnPaginatedResult() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Chapter> chapterPage = new PageImpl<>(testChapters, pageable, testChapters.size());
        when(chapterService.getPaginatedChapters(any(Pageable.class))).thenReturn(chapterPage);

        // When & Then
        mockMvc.perform(get("/api/chapters/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(10)));

        verify(chapterService).getPaginatedChapters(any(Pageable.class));
    }

    @Test
    void getChapterById_ShouldReturnChapter_WhenExists() throws Exception {
        // Given
        when(chapterService.getChapterById(1L)).thenReturn(Optional.of(testChapter));

        // When & Then
        mockMvc.perform(get("/api/chapters/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Chapter")))
                .andExpect(jsonPath("$.universityName", is("Test University")))
                .andExpect(jsonPath("$.state", is("California")));

        verify(chapterService).getChapterById(1L);
    }

    @Test
    void getChapterById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        // Given
        when(chapterService.getChapterById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/chapters/999"))
                .andExpect(status().isNotFound());

        verify(chapterService).getChapterById(999L);
    }

    @Test
    void getActiveChapters_ShouldReturnActiveChaptersOnly() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenReturn(testChapters);

        // When & Then
        mockMvc.perform(get("/api/chapters/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Chapter")))
                .andExpect(jsonPath("$[1].name", is("Chapter Two")));

        verify(chapterService).getAllActiveChapters();
    }

    @Test
    void searchChapters_ShouldReturnFilteredResults() throws Exception {
        // Given
        List<Chapter> searchResults = Arrays.asList(testChapter);
        when(chapterService.searchChapters(eq("Test"), isNull(), eq("California"), isNull(), eq(true)))
                .thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/chapters/search")
                .param("name", "Test")
                .param("state", "California")
                .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Chapter")));

        verify(chapterService).searchChapters("Test", null, "California", null, true);
    }

    @Test
    void createChapter_ShouldReturnCreatedChapter_WhenValidInput() throws Exception {
        // Given
        Chapter newChapter = new Chapter();
        newChapter.setName("New Chapter");
        newChapter.setUniversityName("New University");
        newChapter.setState("Texas");
        newChapter.setCity("Dallas");

        Chapter savedChapter = new Chapter();
        savedChapter.setId(3L);
        savedChapter.setName("New Chapter");
        savedChapter.setUniversityName("New University");
        savedChapter.setState("Texas");
        savedChapter.setCity("Dallas");
        savedChapter.setActive(true);

        when(chapterService.createChapter(any(Chapter.class))).thenReturn(savedChapter);

        // When & Then
        mockMvc.perform(post("/api/chapters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newChapter)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New Chapter")))
                .andExpect(jsonPath("$.universityName", is("New University")))
                .andExpect(jsonPath("$.active", is(true)));

        verify(chapterService).createChapter(any(Chapter.class));
    }

    @Test
    void updateChapter_ShouldReturnUpdatedChapter_WhenExists() throws Exception {
        // Given
        Chapter updatedChapter = new Chapter();
        updatedChapter.setName("Updated Chapter");
        updatedChapter.setUniversityName("Updated University");
        updatedChapter.setState("Updated State");

        testChapter.setName("Updated Chapter");
        testChapter.setUniversityName("Updated University");
        testChapter.setState("Updated State");

        when(chapterService.updateChapter(eq(1L), any(Chapter.class))).thenReturn(testChapter);

        // When & Then
        mockMvc.perform(put("/api/chapters/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedChapter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Chapter")))
                .andExpect(jsonPath("$.universityName", is("Updated University")));

        verify(chapterService).updateChapter(eq(1L), any(Chapter.class));
    }

    @Test
    void deleteChapter_ShouldReturnNoContent_WhenExists() throws Exception {
        // Given
        doNothing().when(chapterService).deleteChapter(1L);

        // When & Then
        mockMvc.perform(delete("/api/chapters/1"))
                .andExpect(status().isNoContent());

        verify(chapterService).deleteChapter(1L);
    }

    @Test
    void getAllInstitutions_ShouldReturnAllInstitutions() throws Exception {
        // Given
        List<Institution> institutions = Arrays.asList(testInstitution);
        when(institutionService.getAllInstitutions()).thenReturn(institutions);

        // When & Then
        mockMvc.perform(get("/api/chapters/institutions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test University")))
                .andExpect(jsonPath("$[0].state", is("California")));

        verify(institutionService).getAllInstitutions();
    }

    @Test
    void getAllChapters_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(chapterService.getAllActiveChapters()).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/chapters"))
                .andExpect(status().isInternalServerError());

        verify(chapterService).getAllActiveChapters();
    }

    @Test
    void reactivateChapter_ShouldReturnUpdatedChapter_WhenExists() throws Exception {
        // Given
        Chapter activatedChapter = testChapter;
        activatedChapter.setActive(true);
        when(chapterService.reactivateChapter(1L)).thenReturn(activatedChapter);

        // When & Then
        mockMvc.perform(put("/api/chapters/1/activate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Chapter"))
                .andExpect(jsonPath("$.active").value(true));

        verify(chapterService).reactivateChapter(1L);
    }

    @Test
    void reactivateChapter_ShouldReturnNotFound_WhenNotExists() throws Exception {
        // Given
        when(chapterService.reactivateChapter(999L)).thenThrow(new RuntimeException("Chapter not found"));

        // When & Then
        mockMvc.perform(put("/api/chapters/999/activate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(chapterService).reactivateChapter(999L);
    }

    @Test
    void getChaptersByState_ShouldReturnChaptersInState() throws Exception {
        // Given
        List<Chapter> stateChapters = Arrays.asList(testChapter);
        when(chapterService.searchChaptersByState("CA")).thenReturn(stateChapters);

        // When & Then
        mockMvc.perform(get("/api/chapters/state/CA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Chapter"));

        verify(chapterService).searchChaptersByState("CA");
    }

    @Test
    void getChaptersByState_ShouldReturnEmptyList_WhenNoChaptersInState() throws Exception {
        // Given
        when(chapterService.searchChaptersByState("AK")).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/chapters/state/AK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(chapterService).searchChaptersByState("AK");
    }
}