package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find member by email
    Optional<Member> findByEmail(String email);

    // Find members by chapter ID
    List<Member> findByChapterId(Long chapterId);

    // Find active members by chapter ID
    List<Member> findByChapterIdAndActiveTrue(Long chapterId);

    // Find members by role
    List<Member> findByRole(MemberRole role);

    // Find members by chapter ID and role
    List<Member> findByChapterIdAndRole(Long chapterId, MemberRole role);

    // Find active members by chapter ID and role
    List<Member> findByChapterIdAndRoleAndActiveTrue(Long chapterId, MemberRole role);

    // Find members by first and last name (case-insensitive)
    List<Member> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    // Find members by graduation year
    List<Member> findByGraduationYear(String graduationYear);

    // Find members by major (case-insensitive)
    List<Member> findByMajorContainingIgnoreCase(String major);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if email exists for a different member (for updates)
    boolean existsByEmailAndIdNot(String email, Long id);

    // Custom query to find chapter officers (President, Vice President, Secretary,
    // Treasurer, Officer)
    @Query("SELECT m FROM Member m WHERE m.chapter.id = :chapterId AND m.role IN ('PRESIDENT', 'VICE_PRESIDENT', 'SECRETARY', 'TREASURER', 'OFFICER') AND m.active = true ORDER BY m.role")
    List<Member> findChapterOfficers(@Param("chapterId") Long chapterId);

    // Custom query to search members by multiple criteria within a chapter
    @Query("SELECT m FROM Member m WHERE m.chapter.id = :chapterId AND " +
            "(:firstName IS NULL OR LOWER(m.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:email IS NULL OR LOWER(m.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:role IS NULL OR m.role = :role) AND " +
            "(:major IS NULL OR LOWER(m.major) LIKE LOWER(CONCAT('%', :major, '%'))) AND " +
            "(:graduationYear IS NULL OR m.graduationYear = :graduationYear) AND " +
            "(:active IS NULL OR m.active = :active)")
    List<Member> findMembersByCriteria(
            @Param("chapterId") Long chapterId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("role") MemberRole role,
            @Param("major") String major,
            @Param("graduationYear") String graduationYear,
            @Param("active") Boolean active);

    // Count active members by chapter
    @Query("SELECT COUNT(m) FROM Member m WHERE m.chapter.id = :chapterId AND m.active = true")
    Long countActiveMembersByChapter(@Param("chapterId") Long chapterId);

    // Count members by role in a chapter
    @Query("SELECT COUNT(m) FROM Member m WHERE m.chapter.id = :chapterId AND m.role = :role AND m.active = true")
    Long countMembersByRoleInChapter(@Param("chapterId") Long chapterId, @Param("role") MemberRole role);

    // Find the president of a chapter
    @Query("SELECT m FROM Member m WHERE m.chapter.id = :chapterId AND m.role = 'PRESIDENT' AND m.active = true")
    Optional<Member> findChapterPresident(@Param("chapterId") Long chapterId);
}