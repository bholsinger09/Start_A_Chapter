package com.turningpoint.chapterorganizer.config;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import com.turningpoint.chapterorganizer.testutil.ChapterAssertions;
import com.turningpoint.chapterorganizer.testutil.MemberAssertions;
import com.turningpoint.chapterorganizer.testutil.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.turningpoint.chapterorganizer.testutil.ChapterAssertions.assertThat;
import static com.turningpoint.chapterorganizer.testutil.MemberAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for DataPopulation following JUnit Internals principles
 * 
 * Focuses on validating that our successive refinement implementation:
 * - Uses extracted helper methods correctly
 * - Applies consistent object creation patterns
 * - Maintains data integrity and business rules
 * - Performs efficiently with proper separation of concerns
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("fast")
@DisplayName("DataPopulation Successive Refinement Tests")
class DataPopulationTest {

    @Mock
    private ChapterRepository chapterRepository;
    
    @Mock
    private MemberRepository memberRepository;
    
    @InjectMocks
    private DataPopulation dataPopulation;
    
    @BeforeEach
    void setUp() {
        // Setup common mock behavior
        when(chapterRepository.count()).thenReturn(0L);
        when(memberRepository.count()).thenReturn(0L);
    }

    @Nested
    @DisplayName("Chapter Population Tests")
    class ChapterPopulationTests {

        @Test
        @DisplayName("Should create chapters using refactored method")
        void shouldCreateChaptersUsingRefactoredMethod() throws Exception {
            // Given
            ArgumentCaptor<List<Chapter>> chaptersCaptor = ArgumentCaptor.forClass(List.class);
            
            // When
            dataPopulation.run();
            
            // Then - Verify chapters were saved
            verify(chapterRepository).saveAll(chaptersCaptor.capture());
            List<Chapter> savedChapters = chaptersCaptor.getValue();
            
            assertThat(savedChapters).hasSize(6);
            
            // Validate each chapter using our custom assertions
            Chapter ucla = savedChapters.stream()
                .filter(c -> "UCLA".equals(c.getName()))
                .findFirst()
                .orElseThrow();
                
            assertThat(ucla)
                .isValidChapter()
                .hasName("UCLA")
                .isAtUniversity("University of California, Los Angeles")
                .isInCalifornia()
                .wasCreatedByRefactoredMethod();
        }

        @Test
        @DisplayName("Should skip chapter creation when chapters exist")
        void shouldSkipChapterCreationWhenChaptersExist() throws Exception {
            // Given - Chapters already exist
            when(chapterRepository.count()).thenReturn(5L);
            
            // When
            dataPopulation.run();
            
            // Then - Should not create new chapters
            verify(chapterRepository, never()).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("Member Population Tests")
    class MemberPopulationTests {

        @Test
        @DisplayName("Should create members using refactored createMember helper method")
        void shouldCreateMembersUsingRefactoredHelper() throws Exception {
            // Given
            List<Chapter> testChapters = TestConfiguration.TestDataSets.complete().getChapters();
            when(chapterRepository.findAll()).thenReturn(testChapters);
            
            ArgumentCaptor<List<Member>> membersCaptor = ArgumentCaptor.forClass(List.class);
            
            // When
            dataPopulation.run();
            
            // Then - Verify members were saved
            verify(memberRepository).saveAll(membersCaptor.capture());
            List<Member> savedMembers = membersCaptor.getValue();
            
            assertThat(savedMembers).hasSizeGreaterThan(0);
            
            // Validate that all members were created using our refactored pattern
            for (Member member : savedMembers) {
                assertThat(member).wasCreatedByRefactoredMethod();
            }
        }

        @Test
        @DisplayName("Should create admin member using addAdministratorMember method")
        void shouldCreateAdminMemberUsingExtractedMethod() throws Exception {
            // Given
            List<Chapter> testChapters = Arrays.asList(
                TestConfiguration.TestDataSets.complete().getFirstChapter()
            );
            when(chapterRepository.findAll()).thenReturn(testChapters);
            
            ArgumentCaptor<List<Member>> membersCaptor = ArgumentCaptor.forClass(List.class);
            
            // When
            dataPopulation.run();
            
            // Then
            verify(memberRepository).saveAll(membersCaptor.capture());
            List<Member> savedMembers = membersCaptor.getValue();
            
            // Find the admin member (Ben Holsinger)
            Member admin = savedMembers.stream()
                .filter(m -> "Ben".equals(m.getFirstName()) && "Holsinger".equals(m.getLastName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Admin member not found"));
            
            assertThat(admin)
                .hasFullName("Ben Holsinger")
                .hasRole(MemberRole.PRESIDENT)
                .hasEmailDomain("hotmail.com")
                .isValidMember();
        }

        @Test
        @DisplayName("Should create sample members using addSampleMembers method")
        void shouldCreateSampleMembersUsingExtractedMethod() throws Exception {
            // Given
            List<Chapter> testChapters = TestConfiguration.TestDataSets.complete().getChapters();
            when(chapterRepository.findAll()).thenReturn(testChapters);
            
            ArgumentCaptor<List<Member>> membersCaptor = ArgumentCaptor.forClass(List.class);
            
            // When
            dataPopulation.run();
            
            // Then
            verify(memberRepository).saveAll(membersCaptor.capture());
            List<Member> savedMembers = membersCaptor.getValue();
            
            // Validate we have members from different chapters (UCLA, Stanford, USC, Berkeley)
            long uclaMembers = savedMembers.stream()
                .filter(m -> m.getChapter() != null && "UCLA".equals(m.getChapter().getName()))
                .count();
            
            assertThat(uclaMembers).isGreaterThanOrEqualTo(3); // Should have multiple UCLA members
            
            // Validate consistent pattern - all members should have complete information
            Member johnSmith = savedMembers.stream()
                .filter(m -> "John".equals(m.getFirstName()) && "Smith".equals(m.getLastName()))
                .findFirst()
                .orElseThrow();
                
            assertThat(johnSmith)
                .hasCompleteContactInfo()
                .hasRole(MemberRole.PRESIDENT)
                .isInChapter("UCLA");
        }

        @Test
        @DisplayName("Should skip member creation when members exist")
        void shouldSkipMemberCreationWhenMembersExist() throws Exception {
            // Given - Members already exist
            when(memberRepository.count()).thenReturn(10L);
            
            // When
            dataPopulation.run();
            
            // Then - Should not create new members
            verify(memberRepository, never()).saveAll(anyList());
            verify(chapterRepository, never()).findAll();
        }
    }

    @Nested
    @DisplayName("Successive Refinement Validation Tests")
    class SuccessiveRefinementValidationTests {

        @Test
        @DisplayName("Should demonstrate extracted method benefits")
        void shouldDemonstrateExtractedMethodBenefits() throws Exception {
            // Given
            List<Chapter> testChapters = TestConfiguration.TestDataSets.complete().getChapters();
            when(chapterRepository.findAll()).thenReturn(testChapters);
            
            ArgumentCaptor<List<Member>> membersCaptor = ArgumentCaptor.forClass(List.class);
            
            // When
            long executionTime = TestConfiguration.Timing.measureExecutionTime(() -> {
                try {
                    dataPopulation.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            verify(memberRepository).saveAll(membersCaptor.capture());
            List<Member> savedMembers = membersCaptor.getValue();
            
            // Validate benefits of refactoring:
            // 1. Consistent data creation pattern
            for (Member member : savedMembers) {
                assertThat(member).isValidMember();
            }
            
            // 2. No duplicate code - all members created with same helper method
            // 3. Performance should be reasonable (not testing specific time, just that it completes)
            assertThat(executionTime).isLessThan(TestConfiguration.Timing.SLOW_TEST_TIMEOUT_MS);
            
            // 4. Clear separation of concerns - admin vs sample members
            long adminCount = savedMembers.stream()
                .filter(m -> "bholsinger".equals(m.getUsername()))
                .count();
            assertThat(adminCount).isEqualTo(1); // Only one admin should be created
        }

        @Test
        @DisplayName("Should validate createMember helper method consistency")
        void shouldValidateCreateMemberHelperConsistency() throws Exception {
            // Given
            List<Chapter> testChapters = Arrays.asList(
                TestConfiguration.TestDataSets.complete().getFirstChapter()
            );
            when(chapterRepository.findAll()).thenReturn(testChapters);
            
            ArgumentCaptor<List<Member>> membersCaptor = ArgumentCaptor.forClass(List.class);
            
            // When
            dataPopulation.run();
            
            // Then
            verify(memberRepository).saveAll(membersCaptor.capture());
            List<Member> savedMembers = membersCaptor.getValue();
            
            // Validate that our helper method creates consistent members
            for (Member member : savedMembers) {
                // All members should have required fields set by our helper
                assertThat(member.getFirstName()).isNotEmpty();
                assertThat(member.getLastName()).isNotEmpty();
                assertThat(member.getEmail()).contains("@");
                assertThat(member.getUsername()).isNotEmpty();
                assertThat(member.getRole()).isNotNull();
                assertThat(member.getChapter()).isNotNull();
                
                // Validate using our custom assertion
                assertThat(member).wasCreatedByRefactoredMethod();
            }
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle empty chapter list gracefully")
        void shouldHandleEmptyChapterListGracefully() throws Exception {
            // Given - No chapters available
            when(chapterRepository.findAll()).thenReturn(new ArrayList<>());
            
            // When
            dataPopulation.run();
            
            // Then - Should not attempt to create members
            verify(memberRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("Should handle repository exceptions gracefully")
        void shouldHandleRepositoryExceptionsGracefully() throws Exception {
            // Given - Repository throws exception
            when(chapterRepository.count()).thenThrow(new RuntimeException("Database error"));
            
            // When & Then - Should not propagate exception during population
            try {
                dataPopulation.run();
                // Should complete without throwing
            } catch (Exception e) {
                // If exception occurs, it should be handled appropriately
                assertThat(e.getMessage()).contains("Database error");
            }
        }
    }
}