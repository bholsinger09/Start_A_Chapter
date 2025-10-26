package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.*;
import com.turningpoint.chapterorganizer.repository.ChurchRepository;
import com.turningpoint.chapterorganizer.repository.InstitutionRepository;
import com.turningpoint.chapterorganizer.repository.UniversityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private ChurchRepository churchRepository;

    @InjectMocks
    private InstitutionService institutionService;

    private University testUniversity;
    private Church testChurch;

    @BeforeEach
    void setUp() {
        testUniversity = new University();
        testUniversity.setId(1L);
        testUniversity.setName("Test University");
        testUniversity.setState("California");
        testUniversity.setCity("Los Angeles");

        testChurch = new Church();
        testChurch.setId(1L);
        testChurch.setName("Test Church");
        testChurch.setState("Texas");
        testChurch.setCity("Dallas");
    }

    @Test
    void getAllInstitutions_ShouldReturnInstitutions_WhenInstitutionsExist() {
        // Given
        List<Institution> institutions = Arrays.asList(testUniversity);
        when(institutionRepository.findAll()).thenReturn(institutions);

        // When
        List<Institution> result = institutionService.getAllInstitutions();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUniversity.getName(), result.get(0).getName());
        verify(institutionRepository).findAll();
    }

    @Test
    void getAllInstitutions_ShouldCreateDefaultInstitutions_WhenEmpty() {
        // Given
        when(institutionRepository.findAll()).thenReturn(Collections.emptyList()).thenReturn(Arrays.asList(testUniversity));
        when(institutionRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(universityRepository.save(any(University.class))).thenReturn(testUniversity);

        // When
        List<Institution> result = institutionService.getAllInstitutions();

        // Then
        assertEquals(1, result.size());
        verify(institutionRepository, times(2)).findAll();
        verify(universityRepository, atLeastOnce()).save(any(University.class));
    }    @Test
    void getInstitutionById_ShouldReturnInstitution_WhenExists() {
        // Given
        when(institutionRepository.findById(1L)).thenReturn(Optional.of(testUniversity));

        // When
        Optional<Institution> result = institutionService.getInstitutionById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUniversity.getName(), result.get().getName());
        verify(institutionRepository).findById(1L);
    }

    @Test
    void getInstitutionById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        when(institutionRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Institution> result = institutionService.getInstitutionById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(institutionRepository).findById(999L);
    }

    @Test
    void getInstitutionByName_ShouldReturnInstitution_WhenExists() {
        // Given
        when(institutionRepository.findByNameIgnoreCase("Test University")).thenReturn(Optional.of(testUniversity));

        // When
        Optional<Institution> result = institutionService.getInstitutionByName("Test University");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUniversity.getName(), result.get().getName());
        verify(institutionRepository).findByNameIgnoreCase("Test University");
    }

    @Test
    void searchInstitutions_ShouldReturnMatchingInstitutions() {
        // Given
        when(institutionRepository.findByNameContainingIgnoreCase("Test")).thenReturn(Arrays.asList(testUniversity));

        // When
        List<Institution> result = institutionService.searchInstitutions("Test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUniversity.getName(), result.get(0).getName());
        verify(institutionRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void getInstitutionsByState_ShouldReturnFilteredInstitutions() {
        // Given
        when(institutionRepository.findByStateIgnoreCase("California")).thenReturn(Arrays.asList(testUniversity));

        // When
        List<Institution> result = institutionService.getInstitutionsByState("California");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("California", result.get(0).getState());
        verify(institutionRepository).findByStateIgnoreCase("California");
    }

    @Test
    void deleteInstitution_ShouldCallRepository() {
        // When
        institutionService.deleteInstitution(1L);

        // Then
        verify(institutionRepository).deleteById(1L);
    }

    @Test
    void getAllUniversities_ShouldReturnUniversities() {
        // Given
        List<University> universities = Arrays.asList(testUniversity);
        when(universityRepository.findAll()).thenReturn(universities);

        // When
        List<University> result = institutionService.getAllUniversities();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUniversity.getName(), result.get(0).getName());
        verify(universityRepository).findAll();
    }

    @Test
    void getAllChurches_ShouldReturnChurches() {
        // Given
        List<Church> churches = Arrays.asList(testChurch);
        when(churchRepository.findAll()).thenReturn(churches);

        // When
        List<Church> result = institutionService.getAllChurches();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testChurch.getName(), result.get(0).getName());
        verify(churchRepository).findAll();
    }

    @Test
    void createUniversity_ShouldReturnSavedUniversity() {
        // Given
        when(universityRepository.save(testUniversity)).thenReturn(testUniversity);

        // When
        University result = institutionService.createUniversity(testUniversity);

        // Then
        assertNotNull(result);
        assertEquals(testUniversity.getName(), result.getName());
        verify(universityRepository).save(testUniversity);
    }

    @Test
    void createChurch_ShouldReturnSavedChurch() {
        // Given
        when(churchRepository.save(testChurch)).thenReturn(testChurch);

        // When
        Church result = institutionService.createChurch(testChurch);

        // Then
        assertNotNull(result);
        assertEquals(testChurch.getName(), result.getName());
        verify(churchRepository).save(testChurch);
    }

    @Test
    void institutionExists_ShouldReturnTrue_WhenExists() {
        // Given
        when(institutionRepository.existsByNameIgnoreCase("Test University")).thenReturn(true);

        // When
        boolean result = institutionService.institutionExists("Test University");

        // Then
        assertTrue(result);
        verify(institutionRepository).existsByNameIgnoreCase("Test University");
    }

    @Test
    void institutionExists_ShouldReturnFalse_WhenNotExists() {
        // Given
        when(institutionRepository.existsByNameIgnoreCase("Non-existent University")).thenReturn(false);

        // When
        boolean result = institutionService.institutionExists("Non-existent University");

        // Then
        assertFalse(result);
        verify(institutionRepository).existsByNameIgnoreCase("Non-existent University");
    }

    @Test
    void getAllInstitutions_ShouldHandleException() {
        // Given
        when(institutionRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> institutionService.getAllInstitutions());
        assertNotNull(thrown);
        verify(institutionRepository).findAll();
    }
}