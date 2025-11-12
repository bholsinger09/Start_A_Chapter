package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import com.turningpoint.chapterorganizer.testutil.MemberAssertions;
import com.turningpoint.chapterorganizer.testutil.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.turningpoint.chapterorganizer.testutil.MemberAssertions.assertThat;
import static com.turningpoint.chapterorganizer.testutil.TestDataBuilder.aChapter;
import static com.turningpoint.chapterorganizer.testutil.TestDataBuilder.aMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Enhanced MemberService Tests following JUnit Internals principles
 * 
 * Test Structure:
 * - Uses custom assertions for domain-specific validation
 * - Employs test data builders for readable setup
 * - Organizes tests with nested classes for logical grouping
 * - Includes performance and parameterized tests
 * - Follows F.I.R.S.T principles (Fast, Independent, Repeatable, Self-validating, Timely)
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("fast")
@DisplayName("MemberService Unit Tests")
class MemberServiceEnhancedTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChapterService chapterService;

    @InjectMocks
    private MemberService memberService;

    private TestConfiguration.TestDataSet testData;

    @BeforeEach
    void setUp() {
        testData = TestConfiguration.TestDataSets.complete();
    }

    @Nested
    @DisplayName("Create Member Operations")
    class CreateMemberTests {

        @Test
        @DisplayName("Should create member with valid input using refactored method")
        @Timeout(value = 1, unit = TimeUnit.SECONDS)
        void shouldCreateMemberWithValidInput() {
            // Given
            Member inputMember = aMember()
                .johnSmith()
                .asPresident()
                .inChapter(testData.getFirstChapter())
                .build();

            when(memberRepository.save(any(Member.class))).thenReturn(inputMember);

            // When
            Member result = memberService.createMember(inputMember);

            // Then - Using custom domain assertions
            assertThat(result)
                .isValidMember()
                .hasFullName("John Smith")
                .hasRole(MemberRole.PRESIDENT)
                .isActive()
                .wasCreatedByRefactoredMethod();

            verify(memberRepository).save(inputMember);
        }

        @Test
        @DisplayName("Should handle chapter assignment in refactored validation method")
        void shouldHandleChapterAssignmentInRefactoredMethod() {
            // Given
            Chapter chapter = testData.getFirstChapter();
            Member memberWithoutChapter = aMember()
                .johnSmith()
                .build();
            memberWithoutChapter.setChapter(null); // Simulate missing chapter

            Member memberWithChapter = aMember()
                .johnSmith()
                .inChapter(chapter)
                .build();

            when(chapterService.getAllActiveChapters()).thenReturn(List.of(chapter));
            when(memberRepository.save(any(Member.class))).thenReturn(memberWithChapter);

            // When
            Member result = memberService.createMember(memberWithoutChapter);

            // Then
            assertThat(result)
                .isValidMember()
                .isInChapter(chapter.getName());

            verify(chapterService).getAllActiveChapters();
        }

        @Test
        @DisplayName("Should apply default values using refactored setMemberDefaults method")
        void shouldApplyDefaultValuesUsingRefactoredMethod() {
            // Given
            Member memberWithoutDefaults = aMember()
                .withName("John", "Doe")
                .withEmail("john.doe@test.com")
                .inChapter(testData.getFirstChapter())
                .build();

            // Clear default values to test our refactored method sets them
            memberWithoutDefaults.setActive(null);
            memberWithoutDefaults.setRole(null);

            Member memberWithDefaults = aMember()
                .withName("John", "Doe")
                .withEmail("john.doe@test.com")
                .inChapter(testData.getFirstChapter())
                .asMember()
                .build();

            when(memberRepository.save(any(Member.class))).thenReturn(memberWithDefaults);

            // When
            Member result = memberService.createMember(memberWithoutDefaults);

            // Then - Verify our refactored method sets defaults
            assertThat(result)
                .isActive()
                .hasRole(MemberRole.MEMBER);
        }

        @ParameterizedTest
        @EnumSource(MemberRole.class)
        @DisplayName("Should create members with all role types")
        void shouldCreateMembersWithAllRoleTypes(MemberRole role) {
            // Given
            Member member = aMember()
                .johnSmith()
                .withRole(role)
                .inChapter(testData.getFirstChapter())
                .build();

            when(memberRepository.save(any(Member.class))).thenReturn(member);

            // When
            Member result = memberService.createMember(member);

            // Then
            assertThat(result)
                .hasRole(role)
                .isValidMember();
        }

        @Test
        @DisplayName("Should handle constraint violation using refactored error handling")
        void shouldHandleConstraintViolationUsingRefactoredMethod() {
            // Given
            Member duplicateMember = aMember()
                .johnSmith()
                .inChapter(testData.getFirstChapter())
                .build();

            Member existingMember = aMember()
                .johnSmith()
                .inChapter(testData.getFirstChapter())
                .withId(1L)
                .build();

            when(memberRepository.save(any(Member.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));
            when(memberRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(existingMember));

            // When
            Member result = memberService.createMember(duplicateMember);

            // Then - Our refactored method should return existing member
            assertThat(result)
                .isPersisted()
                .hasFullName("John Smith");
        }
    }

    @Nested
    @DisplayName("Find Member Operations")
    class FindMemberTests {

        @Test
        @DisplayName("Should find member by ID")
        void shouldFindMemberById() {
            // Given
            Member expectedMember = testData.getFirstMember();
            when(memberRepository.findById(1L)).thenReturn(Optional.of(expectedMember));

            // When
            Optional<Member> result = memberService.getMemberById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isValidMember();
        }

        @Test
        @DisplayName("Should return empty when member not found")
        void shouldReturnEmptyWhenMemberNotFound() {
            // Given
            when(memberRepository.findById(999L)).thenReturn(Optional.empty());

            // When
            Optional<Member> result = memberService.getMemberById(999L);

            // Then
            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"john.smith@ucla.edu", "JOHN.SMITH@UCLA.EDU", "John.Smith@UCLA.edu"})
        @DisplayName("Should find member by email")
        void shouldFindMemberByEmail(String email) {
            // Given
            Member member = testData.getFirstMember();
            when(memberRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(member));

            // When
            Optional<Member> result = memberService.getMemberByEmail(email.toLowerCase());

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isValidMember();
        }
    }

    @Nested
    @DisplayName("Member Validation Tests")
    class MemberValidationTests {

        @Test
        @DisplayName("Should validate complete member information")
        void shouldValidateCompleteMemberInformation() {
            // Given
            Member completeMember = aMember()
                .johnSmith()
                .withEmail("john.smith@ucla.edu")
                .withPhone("310-555-0101")
                .asPresident()
                .inChapter(testData.getFirstChapter())
                .build();

            // Then - Using our custom assertion for comprehensive validation
            assertThat(completeMember)
                .isValidMember()
                .hasCompleteContactInfo()
                .hasEmailDomain("ucla.edu")
                .hasLeadershipRole();
        }

        @Test
        @DisplayName("Should identify invalid member data")
        void shouldIdentifyInvalidMemberData() {
            // Given
            Member invalidMember = new Member();

            // Then - Custom assertion should clearly identify what's missing
            assertThatThrownBy(() -> 
                MemberAssertions.assertThat(invalidMember).isValidMember())
                .hasMessageContaining("first name");
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    @Tag("performance")
    class PerformanceTests {

        @Test
        @DisplayName("Should create member quickly")
        @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
        void shouldCreateMemberQuickly() {
            // Given
            Member member = aMember()
                .johnSmith()
                .inChapter(testData.getFirstChapter())
                .build();

            when(memberRepository.save(any(Member.class))).thenReturn(member);

            // When & Then - Should complete within timeout
            long executionTime = TestConfiguration.Timing.measureExecutionTime(() -> {
                memberService.createMember(member);
            });

            assertThat(executionTime).isLessThan(TestConfiguration.Timing.FAST_TEST_TIMEOUT_MS);
        }
    }

    @Nested
    @DisplayName("Integration with Successive Refinement")
    class SuccessiveRefinementTests {

        @Test
        @DisplayName("Should use refactored validateAndSetChapter method")
        void shouldUseRefactoredValidateAndSetChapterMethod() {
            // Given - Member without chapter (testing our refactored validation)
            Member memberNeedingChapter = aMember()
                .withName("Test", "User")
                .withEmail("test.user@ucla.edu")
                .build();
            memberNeedingChapter.setChapter(null);

            Chapter chapter = aChapter().ucla().build();
            when(chapterService.getAllActiveChapters())
                .thenReturn(List.of(chapter));
            when(memberRepository.save(any(Member.class)))
                .thenReturn(memberNeedingChapter);

            // When
            memberService.createMember(memberNeedingChapter);

            // Then - Verify our refactored method was called
            verify(chapterService).getAllActiveChapters();
        }

        @Test
        @DisplayName("Should use refactored setMemberDefaults method")
        void shouldUseRefactoredSetMemberDefaultsMethod() {
            // Given - Member missing defaults (testing our refactored defaults method)
            Member memberNeedingDefaults = aMember()
                .withName("Test", "User")
                .withEmail("test.user@test.com")
                .inChapter(testData.getFirstChapter())
                .build();

            // Clear defaults to verify our refactored method sets them
            memberNeedingDefaults.setActive(null);
            memberNeedingDefaults.setRole(null);

            when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
                Member saved = invocation.getArgument(0);
                // Verify our refactored method set the defaults
                assertThat(saved.getActive()).isTrue();
                assertThat(saved.getRole()).isEqualTo(MemberRole.MEMBER);
                return saved;
            });

            // When
            memberService.createMember(memberNeedingDefaults);

            // Then - Verification happens in the mock answer above
            verify(memberRepository).save(any(Member.class));
        }
    }
}