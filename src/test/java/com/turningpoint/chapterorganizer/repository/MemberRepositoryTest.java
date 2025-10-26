package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for MemberRepository
 * Tests all finder methods, custom queries, and analytics functions
 */
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    private Chapter testChapter1;
    private Chapter testChapter2;
    private Member testMember1;
    private Member testMember2;
    private Member testMember3;

    @BeforeEach
    void setUp() {
        // Create test chapters
        testChapter1 = new Chapter();
        testChapter1.setName("Alpha Chapter");
        testChapter1.setUniversityName("Test University");
        testChapter1.setState("California");
        testChapter1.setCity("Los Angeles");
        testChapter1.setActive(true);
        testChapter1.setCreatedAt(LocalDateTime.now().minusDays(100));
        testChapter1 = entityManager.persistAndFlush(testChapter1);

        testChapter2 = new Chapter();
        testChapter2.setName("Beta Chapter");
        testChapter2.setUniversityName("Another University");
        testChapter2.setState("Texas");
        testChapter2.setCity("Austin");
        testChapter2.setActive(true);
        testChapter2.setCreatedAt(LocalDateTime.now().minusDays(50));
        testChapter2 = entityManager.persistAndFlush(testChapter2);

        // Create test members with specific dates for testing
        LocalDateTime baseTime = LocalDateTime.now().minusDays(30);
        
        testMember1 = new Member();
        testMember1.setFirstName("John");
        testMember1.setLastName("Doe");
        testMember1.setEmail("john.doe@example.com");
        testMember1.setUsername("johndoe");
        testMember1.setRole(MemberRole.PRESIDENT);
        testMember1.setMajor("Computer Science");
        testMember1.setGraduationYear("2024");
        testMember1.setActive(true);
        testMember1.setChapter(testChapter1);
        testMember1.setCreatedAt(baseTime.minusDays(5)); // 35 days ago
        testMember1 = entityManager.persistAndFlush(testMember1);

        testMember2 = new Member();
        testMember2.setFirstName("Jane");
        testMember2.setLastName("Smith");
        testMember2.setEmail("jane.smith@example.com");
        testMember2.setUsername("janesmith");
        testMember2.setRole(MemberRole.VICE_PRESIDENT);
        testMember2.setMajor("Business Administration");
        testMember2.setGraduationYear("2025");
        testMember2.setActive(true);
        testMember2.setChapter(testChapter1);
        testMember2.setCreatedAt(baseTime); // 30 days ago
        testMember2 = entityManager.persistAndFlush(testMember2);

        testMember3 = new Member();
        testMember3.setFirstName("Bob");
        testMember3.setLastName("Johnson");
        testMember3.setEmail("bob.johnson@example.com");
        testMember3.setUsername("bobjohnson");
        testMember3.setRole(MemberRole.MEMBER);
        testMember3.setMajor("Engineering");
        testMember3.setGraduationYear("2024");
        testMember3.setActive(false);
        testMember3.setChapter(testChapter2);
        testMember3.setCreatedAt(baseTime.plusDays(10)); // 20 days ago
        testMember3 = entityManager.persistAndFlush(testMember3);

        entityManager.clear();
    }

    // =========================================
    // Basic Finder Method Tests
    // =========================================

    @Test
    void findByEmail_ShouldReturnMember_WhenEmailExists() {
        // When
        Optional<Member> result = memberRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
        assertThat(result.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // When
        Optional<Member> result = memberRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_ShouldReturnMember_WhenUsernameExists() {
        // When
        Optional<Member> result = memberRepository.findByUsername("johndoe");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenUsernameDoesNotExist() {
        // When
        Optional<Member> result = memberRepository.findByUsername("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    // =========================================
    // Chapter-based Finder Tests
    // =========================================

    @Test
    void findByChapter_Id_ShouldReturnMembersInChapter() {
        // When
        List<Member> result = memberRepository.findByChapter_Id(testChapter1.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Member::getEmail)
                .containsExactlyInAnyOrder("john.doe@example.com", "jane.smith@example.com");
    }

    @Test
    void findByChapter_IdAndActiveTrue_ShouldReturnOnlyActiveMembers() {
        // When
        List<Member> result = memberRepository.findByChapter_IdAndActiveTrue(testChapter1.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Member::getActive);
    }

    @Test
    void findByChapter_IdAndActiveTrue_ShouldReturnEmptyForInactiveMembers() {
        // When
        List<Member> result = memberRepository.findByChapter_IdAndActiveTrue(testChapter2.getId());

        // Then
        assertThat(result).isEmpty(); // testMember3 is inactive
    }

    // =========================================
    // Role-based Finder Tests
    // =========================================

    @Test
    void findByRole_ShouldReturnMembersWithSpecificRole() {
        // When
        List<Member> presidents = memberRepository.findByRole(MemberRole.PRESIDENT);
        List<Member> vicePresidents = memberRepository.findByRole(MemberRole.VICE_PRESIDENT);

        // Then
        assertThat(presidents).hasSize(1);
        assertThat(presidents.get(0).getEmail()).isEqualTo("john.doe@example.com");
        
        assertThat(vicePresidents).hasSize(1);
        assertThat(vicePresidents.get(0).getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void findByChapter_IdAndRole_ShouldReturnMembersInChapterWithRole() {
        // When
        List<Member> result = memberRepository.findByChapter_IdAndRole(testChapter1.getId(), MemberRole.PRESIDENT);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findByChapter_IdAndRoleAndActiveTrue_ShouldReturnActiveMembers() {
        // When
        List<Member> result = memberRepository.findByChapter_IdAndRoleAndActiveTrue(
                testChapter1.getId(), MemberRole.PRESIDENT);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActive()).isTrue();
        assertThat(result.get(0).getRole()).isEqualTo(MemberRole.PRESIDENT);
    }

    // =========================================
    // Search and Filter Tests
    // =========================================

    @Test
    void findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase_ShouldReturnMatches() {
        // When
        List<Member> result = memberRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("jo", "jo");

        // Then
        assertThat(result).hasSize(2); // John Doe and Bob Johnson
        assertThat(result).extracting(Member::getFirstName)
                .containsExactlyInAnyOrder("John", "Bob");
    }

    @Test
    void findByGraduationYear_ShouldReturnMembersWithSameGraduationYear() {
        // When
        List<Member> result = memberRepository.findByGraduationYear("2024");

        // Then
        assertThat(result).hasSize(2); // John and Bob both graduate in 2024
        assertThat(result).extracting(Member::getGraduationYear)
                .allMatch(year -> year.equals("2024"));
    }

    @Test
    void findByMajorContainingIgnoreCase_ShouldReturnMembersWithMatchingMajor() {
        // When
        List<Member> result = memberRepository.findByMajorContainingIgnoreCase("computer");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMajor()).isEqualTo("Computer Science");
    }

    // =========================================
    // Existence Check Tests
    // =========================================

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // When
        boolean result = memberRepository.existsByEmail("john.doe@example.com");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // When
        boolean result = memberRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // When
        boolean result = memberRepository.existsByUsername("johndoe");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnFalse_WhenCheckingSameMember() {
        // When
        boolean result = memberRepository.existsByEmailAndIdNot(
                "john.doe@example.com", testMember1.getId());

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnTrue_WhenEmailExistsForDifferentMember() {
        // When
        boolean result = memberRepository.existsByEmailAndIdNot(
                "john.doe@example.com", testMember2.getId());

        // Then
        assertThat(result).isTrue();
    }

    // =========================================
    // Custom Query Tests
    // =========================================

    @Test
    void findChapterOfficers_ShouldReturnOfficersInChapter() {
        // When
        List<Member> result = memberRepository.findChapterOfficers(testChapter1.getId());

        // Then
        assertThat(result).hasSize(2); // President and Vice President
        assertThat(result).extracting(Member::getRole)
                .containsExactlyInAnyOrder(MemberRole.PRESIDENT, MemberRole.VICE_PRESIDENT);
    }

    @Test
    void findMembersByCriteria_ShouldReturnFilteredMembers() {
        // When
        List<Member> result = memberRepository.findMembersByCriteria(
                testChapter1.getId(), 
                "John", // firstName
                null,   // lastName
                null,   // email
                null,   // role
                null,   // major
                null,   // graduationYear
                true    // active
        );

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void findMembersByCriteria_ShouldReturnAllMembers_WhenAllParametersNull() {
        // When
        List<Member> result = memberRepository.findMembersByCriteria(
                testChapter1.getId(), null, null, null, null, null, null, null);

        // Then
        assertThat(result).hasSize(2); // Both members in testChapter1
    }

    @Test
    void findChapterPresident_ShouldReturnPresident_WhenExists() {
        // When
        Optional<Member> result = memberRepository.findChapterPresident(testChapter1.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(MemberRole.PRESIDENT);
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findChapterPresident_ShouldReturnEmpty_WhenNoPresident() {
        // When
        Optional<Member> result = memberRepository.findChapterPresident(testChapter2.getId());

        // Then
        assertThat(result).isEmpty(); // testChapter2 has no president
    }

    // =========================================
    // Count and Analytics Tests
    // =========================================

    @Test
    void countActiveMembersByChapter_ShouldReturnCorrectCount() {
        // When
        Long result = memberRepository.countActiveMembersByChapter(testChapter1.getId());

        // Then
        assertThat(result).isEqualTo(2L);
    }

    @Test
    void countMembersByRoleInChapter_ShouldReturnCorrectCount() {
        // When
        Long presidentsCount = memberRepository.countMembersByRoleInChapter(
                testChapter1.getId(), MemberRole.PRESIDENT);
        Long membersCount = memberRepository.countMembersByRoleInChapter(
                testChapter1.getId(), MemberRole.MEMBER);

        // Then
        assertThat(presidentsCount).isEqualTo(1L);
        assertThat(membersCount).isEqualTo(0L); // No regular members in testChapter1
    }

    @Test
    void countByActiveTrue_ShouldReturnActiveMembersCount() {
        // When
        Long result = memberRepository.countByActiveTrue();

        // Then
        assertThat(result).isEqualTo(2L); // testMember1 and testMember2 are active
    }

    @Test
    void countByChapter_Id_ShouldReturnTotalMembersInChapter() {
        // When
        Long result = memberRepository.countByChapter_Id(testChapter1.getId());

        // Then
        assertThat(result).isEqualTo(2L);
    }

    @Test
    void countByChapter_IdAndActiveTrue_ShouldReturnActiveMembersInChapter() {
        // When
        Long result1 = memberRepository.countByChapter_IdAndActiveTrue(testChapter1.getId());
        Long result2 = memberRepository.countByChapter_IdAndActiveTrue(testChapter2.getId());

        // Then
        assertThat(result1).isEqualTo(2L); // Both members in testChapter1 are active
        assertThat(result2).isEqualTo(0L); // testMember3 in testChapter2 is inactive
    }

    // =========================================
    // Date-based Query Tests (Fixed)
    // =========================================

    @Test
    void countByCreatedAtBetween_ShouldReturnMembersInDateRange() {
        // When - Use a range that includes first two members created
        // Since @CreationTimestamp auto-generates timestamps, we work with actual creation order
        LocalDateTime start = testMember1.getCreatedAt().minusNanos(1000);
        LocalDateTime end = testMember2.getCreatedAt().plusNanos(1000);
        Long result = memberRepository.countByCreatedAtBetween(start, end);

        // Then
        assertThat(result).isEqualTo(2L); // testMember1 and testMember2
    }

    @Test
    void countByCreatedAtAfter_ShouldReturnRecentMembers() {
        // When - Count members created after testMember1's creation time
        // Since @CreationTimestamp auto-generates timestamps, testMember2 and testMember3 
        // will be created after testMember1 (they are persisted later in setUp)
        LocalDateTime after = testMember1.getCreatedAt().plusNanos(1000); // Small buffer to ensure testMember2 and testMember3 are after
        Long result = memberRepository.countByCreatedAtAfter(after);

        // Then
        assertThat(result).isEqualTo(2L); // testMember2 and testMember3 created after testMember1
    }

    @Test
    void findByCreatedAtAfterOrderByCreatedAtDesc_ShouldReturnOrderedRecentMembers() {
        // When - Find members created after testMember1
        // Since @CreationTimestamp auto-generates timestamps, testMember2 and testMember3 are created after testMember1
        LocalDateTime after = testMember1.getCreatedAt().plusNanos(1000);
        List<Member> result = memberRepository.findByCreatedAtAfterOrderByCreatedAtDesc(after);

        // Then
        assertThat(result).hasSize(2); // testMember2 and testMember3
        if (result.size() >= 2) {
            assertThat(result.get(0).getCreatedAt()).isAfterOrEqualTo(result.get(1).getCreatedAt()); // Ordered desc
        }
    }

    // =========================================
    // Edge Cases and Error Scenarios
    // =========================================

    @Test
    void findByChapter_Id_ShouldReturnEmpty_WhenChapterDoesNotExist() {
        // When
        List<Member> result = memberRepository.findByChapter_Id(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findMembersByCriteria_ShouldReturnEmpty_WhenNoMatchingCriteria() {
        // When
        List<Member> result = memberRepository.findMembersByCriteria(
                testChapter1.getId(), 
                "NonExistent", // firstName
                null, null, null, null, null, true
        );

        // Then
        assertThat(result).isEmpty();
    }
}
