package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find member by email
    Optional<Member> findByEmail(String email);
    
    // Find member by username
    Optional<Member> findByUsername(String username);

    // Find members by chapter ID
    List<Member> findByChapter_Id(Long chapterId);

    // Find active members by chapter ID
    List<Member> findByChapter_IdAndActiveTrue(Long chapterId);

    // Find members by role
    List<Member> findByRole(MemberRole role);

    // Find members by chapter ID and role
    List<Member> findByChapter_IdAndRole(Long chapterId, MemberRole role);

    // Find active members by chapter ID and role
    List<Member> findByChapter_IdAndRoleAndActiveTrue(Long chapterId, MemberRole role);

    // Find members by first and last name (case-insensitive)
    List<Member> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    // Find members by graduation year
    List<Member> findByGraduationYear(String graduationYear);

    // Find members by major (case-insensitive)
    List<Member> findByMajorContainingIgnoreCase(String major);

    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if username exists
    boolean existsByUsername(String username);

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

    // Analytics and statistics methods
    
    // Count active members
    Long countByActiveTrue();
    
    // Count members by chapter
    Long countByChapter_Id(Long chapterId);
    
    // Count active members by chapter
    Long countByChapter_IdAndActiveTrue(Long chapterId);
    
    // Date-based queries using createdAt field (join date)
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    Long countByCreatedAtBefore(LocalDateTime before);
    Long countByCreatedAtAfter(LocalDateTime after);
    Long countByActiveTrueAndCreatedAtBefore(LocalDateTime before);
    
    // Find recent members
    List<Member> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime after);
}