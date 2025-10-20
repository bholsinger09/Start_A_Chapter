package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private ChapterService chapterService;

    private Chapter testChapter;

    @BeforeEach
    void setUp() {
        testChapter = new Chapter();
        testChapter.setId(1L);
        testChapter.setName("Test Chapter");
        testChapter.setUniversityName("Test University");
        testChapter.setState("California");
        testChapter.setCity("Los Angeles");
        testChapter.setActive(true);
    }

    @Test
    void createChapter_ShouldReturnSavedChapter_WhenValidInput() {
        // Given
        Chapter newChapter = new Chapter("New Chapter", "New University", "Texas", "Austin");
        when(chapterRepository.existsByNameIgnoreCaseAndUniversityNameIgnoreCase(
                newChapter.getName(), newChapter.getUniversityName())).thenReturn(false);
        when(chapterRepository.save(any(Chapter.class))).thenReturn(newChapter);

        // When
        Chapter result = chapterService.createChapter(newChapter);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Chapter");
        assertThat(result.getUniversityName()).isEqualTo("New University");
        assertThat(result.getActive()).isTrue();
        verify(chapterRepository).save(newChapter);
    }

    @Test
    void createChapter_ShouldThrowException_WhenChapterAlreadyExists() {
        // Given
        when(chapterRepository.existsByNameIgnoreCaseAndUniversityNameIgnoreCase(
                testChapter.getName(), testChapter.getUniversityName())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> chapterService.createChapter(testChapter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chapter with this name already exists at the university");
    }

    @Test
    void getChapterById_ShouldReturnChapter_WhenExists() {
        // Given
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter));

        // When
        Optional<Chapter> result = chapterService.getChapterById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Chapter");
    }

    @Test
    void getChapterById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        when(chapterRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Chapter> result = chapterService.getChapterById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void getAllActiveChapters_ShouldReturnActiveChapters() {
        // Arrange
        List<Chapter> activeChapters = List.of(testChapter);
        when(chapterRepository.findByActiveTrueWithMembers()).thenReturn(activeChapters);

        // Act
        List<Chapter> result = chapterService.getAllActiveChapters();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Chapter");
        assertThat(result.get(0).getActive()).isTrue();

        verify(chapterRepository).findByActiveTrueWithMembers();
    }    @Test
    void updateChapter_ShouldReturnUpdatedChapter_WhenExists() {
        // Given
        Chapter updatedChapter = new Chapter();
        updatedChapter.setName("Updated Chapter");
        updatedChapter.setUniversityName("Updated University");
        updatedChapter.setState("Updated State");
        updatedChapter.setCity("Updated City");

        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter));
        when(chapterRepository.existsByNameIgnoreCaseAndUniversityNameIgnoreCase(
                updatedChapter.getName(), updatedChapter.getUniversityName())).thenReturn(false);
        when(chapterRepository.save(any(Chapter.class))).thenReturn(testChapter);

        // When
        Chapter result = chapterService.updateChapter(1L, updatedChapter);

        // Then
        assertThat(result).isNotNull();
        verify(chapterRepository).save(testChapter);
    }

    @Test
    void updateChapter_ShouldThrowException_WhenNotExists() {
        // Given
        Chapter updatedChapter = new Chapter();
        when(chapterRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chapterService.updateChapter(999L, updatedChapter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chapter not found with id: 999");
    }

    @Test
    void deleteChapter_ShouldMarkAsInactive_WhenExists() {
        // Given
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter));
        when(chapterRepository.save(any(Chapter.class))).thenReturn(testChapter);

        // When
        chapterService.deleteChapter(1L);

        // Then
        assertThat(testChapter.getActive()).isFalse();
        verify(chapterRepository).save(testChapter);
    }

    @Test
    void deleteChapter_ShouldThrowException_WhenNotExists() {
        // Given
        when(chapterRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chapterService.deleteChapter(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chapter not found with id: 999");
    }

    @Test
    void searchChaptersByState_ShouldReturnChapters_WhenStateExists() {
        // Given
        List<Chapter> chapters = Arrays.asList(testChapter);
        when(chapterRepository.findByStateIgnoreCaseAndActive("California", true)).thenReturn(chapters);

        // When
        List<Chapter> result = chapterService.searchChaptersByState("California");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getState()).isEqualToIgnoringCase("California");
    }

    @Test
    void getChapterByName_ShouldReturnChapter_WhenExists() {
        // Given
        when(chapterRepository.findByNameIgnoreCase("Test Chapter")).thenReturn(Optional.of(testChapter));

        // When
        Optional<Chapter> result = chapterService.getChapterByName("Test Chapter");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Chapter");
    }
}