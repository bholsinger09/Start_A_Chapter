package com.turningpoint.chapterorganizer.service.impl;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.MemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MemberQueryServiceImpl.
 * Tests read operations with proper mocking and assertions.
 */
@ExtendWith(MockitoExtension.class)
class MemberQueryServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberQueryServiceImpl memberQueryService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setId(1L);
        testMember.setFirstName("John");
        testMember.setLastName("Doe");
        testMember.setEmail("john.doe@example.com");
        testMember.setUsername("johndoe");
        testMember.setActive(true);
    }

    @Test
    void findById_WithValidId_ShouldReturnMember() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        // When
        Optional<Member> result = memberQueryService.findById(memberId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMember.getId(), result.get().getId());
        verify(memberRepository).findById(memberId);
    }

    @Test
    void findById_WithNullId_ShouldReturnEmpty() {
        // When
        Optional<Member> result = memberQueryService.findById(null);

        // Then
        assertFalse(result.isPresent());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void findById_WithZeroId_ShouldReturnEmpty() {
        // When
        Optional<Member> result = memberQueryService.findById(0L);

        // Then
        assertFalse(result.isPresent());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void findById_WithNegativeId_ShouldReturnEmpty() {
        // When
        Optional<Member> result = memberQueryService.findById(-1L);

        // Then
        assertFalse(result.isPresent());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void findByEmail_WithValidEmail_ShouldReturnMember() {
        // Given
        String email = "john.doe@example.com";
        when(memberRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(testMember));

        // When
        Optional<Member> result = memberQueryService.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMember.getEmail(), result.get().getEmail());
        verify(memberRepository).findByEmail(email.toLowerCase());
    }

    @Test
    void findByEmail_WithUpperCaseEmail_ShouldConvertToLowerCase() {
        // Given
        String upperCaseEmail = "JOHN.DOE@EXAMPLE.COM";
        String lowerCaseEmail = upperCaseEmail.toLowerCase();
        when(memberRepository.findByEmail(lowerCaseEmail)).thenReturn(Optional.of(testMember));

        // When
        Optional<Member> result = memberQueryService.findByEmail(upperCaseEmail);

        // Then
        assertTrue(result.isPresent());
        verify(memberRepository).findByEmail(lowerCaseEmail);
    }

    @Test
    void findByEmail_WithNullEmail_ShouldReturnEmpty() {
        // When
        Optional<Member> result = memberQueryService.findByEmail(null);

        // Then
        assertFalse(result.isPresent());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void findByEmail_WithEmptyEmail_ShouldReturnEmpty() {
        // When
        Optional<Member> result = memberQueryService.findByEmail("   ");

        // Then
        assertFalse(result.isPresent());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void findAll_ShouldReturnAllMembers() {
        // Given
        List<Member> expectedMembers = Arrays.asList(testMember, new Member());
        when(memberRepository.findAll()).thenReturn(expectedMembers);

        // When
        List<Member> result = memberQueryService.findAll();

        // Then
        assertEquals(expectedMembers.size(), result.size());
        assertEquals(expectedMembers, result);
        verify(memberRepository).findAll();
    }

    @Test
    void findActiveByChapterId_WithValidChapterId_ShouldReturnActiveMembers() {
        // Given
        Long chapterId = 1L;
        List<Member> expectedMembers = Arrays.asList(testMember);
        when(memberRepository.findByChapter_IdAndActiveTrue(chapterId)).thenReturn(expectedMembers);

        // When
        List<Member> result = memberQueryService.findActiveByChapterId(chapterId);

        // Then
        assertEquals(expectedMembers.size(), result.size());
        assertEquals(expectedMembers, result);
        verify(memberRepository).findByChapter_IdAndActiveTrue(chapterId);
    }

    @Test
    void findActiveByChapterId_WithNullChapterId_ShouldReturnEmptyList() {
        // When
        List<Member> result = memberQueryService.findActiveByChapterId(null);

        // Then
        assertTrue(result.isEmpty());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void findByChapterAndRole_WithValidParameters_ShouldReturnMembers() {
        // Given
        Long chapterId = 1L;
        MemberRole role = MemberRole.PRESIDENT;
        List<Member> expectedMembers = Arrays.asList(testMember);
        when(memberRepository.findByChapter_IdAndRole(chapterId, role)).thenReturn(expectedMembers);

        // When
        List<Member> result = memberQueryService.findByChapterAndRole(chapterId, role);

        // Then
        assertEquals(expectedMembers.size(), result.size());
        assertEquals(expectedMembers, result);
        verify(memberRepository).findByChapter_IdAndRole(chapterId, role);
    }

    @Test
    void findByChapterAndRole_WithNullRole_ShouldReturnEmptyList() {
        // When
        List<Member> result = memberQueryService.findByChapterAndRole(1L, null);

        // Then
        assertTrue(result.isEmpty());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void searchByName_WithValidSearchTerm_ShouldReturnMembers() {
        // Given
        String searchTerm = "John";
        List<Member> expectedMembers = Arrays.asList(testMember);
        when(memberRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, searchTerm)).thenReturn(expectedMembers);

        // When
        List<Member> result = memberQueryService.searchByName(searchTerm);

        // Then
        assertEquals(expectedMembers.size(), result.size());
        assertEquals(expectedMembers, result);
        verify(memberRepository).findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, searchTerm);
    }

    @Test
    void searchByName_WithNullSearchTerm_ShouldReturnEmptyList() {
        // When
        List<Member> result = memberQueryService.searchByName(null);

        // Then
        assertTrue(result.isEmpty());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void searchByName_WithEmptySearchTerm_ShouldReturnEmptyList() {
        // When
        List<Member> result = memberQueryService.searchByName("   ");

        // Then
        assertTrue(result.isEmpty());
        verifyNoInteractions(memberRepository);
    }
}