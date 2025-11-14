package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.constants.MemberQueryConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find member by email - Boy Scout Rule: Clear naming
    Optional<Member> findMemberByEmail(String email);

    // Find member by username - Boy Scout Rule: Consistent naming pattern
    Optional<Member> findMemberByUsername(String username);

    // Find members by chapter ID
    @Query(MemberQueryConstants.SELECT_MEMBER + " WHERE " + MemberQueryConstants.CHAPTER_MATCH_CONDITION)
    List<Member> findAllMembersByChapterId(@Param(MemberQueryConstants.CHAPTER_ID_PARAM) Long chapterId);

    // Find active members by chapter ID
    @Query(MemberQueryConstants.SELECT_MEMBER + " WHERE " + MemberQueryConstants.CHAPTER_MATCH_CONDITION + " AND " + MemberQueryConstants.ACTIVE_CONDITION)
    List<Member> findActiveMembersByChapterId(@Param(MemberQueryConstants.CHAPTER_ID_PARAM) Long chapterId);

    // Find all members by role - Boy Scout Rule: Descriptive method name
    List<Member> findAllMembersByRole(MemberRole role);

    // Find members by chapter ID and role
    @Query(MemberQueryConstants.SELECT_MEMBER + " WHERE " + MemberQueryConstants.CHAPTER_MATCH_CONDITION + " AND " + MemberQueryConstants.ROLE_MATCH_CONDITION)
    List<Member> findMembersByChapterIdAndRole(@Param(MemberQueryConstants.CHAPTER_ID_PARAM) Long chapterId, @Param(MemberQueryConstants.ROLE_PARAM) MemberRole role);

    // Find active members by chapter ID and role  
    @Query(MemberQueryConstants.SELECT_MEMBER + " WHERE " + MemberQueryConstants.CHAPTER_MATCH_CONDITION + " AND " + MemberQueryConstants.ROLE_MATCH_CONDITION + " AND " + MemberQueryConstants.ACTIVE_CONDITION)
    List<Member> findActiveMembersByChapterIdAndRole(@Param(MemberQueryConstants.CHAPTER_ID_PARAM) Long chapterId, @Param(MemberQueryConstants.ROLE_PARAM) MemberRole role);

    // Find members by partial name match (case-insensitive) - Boy Scout Rule: Clear purpose
    @Query("SELECT m FROM Member m WHERE LOWER(m.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Member> findMembersByPartialNameMatch(@Param("firstName") String firstName, @Param("lastName") String lastName);

    // Find all members by graduation year - Boy Scout Rule: Consistent naming
    List<Member> findAllMembersByGraduationYear(String graduationYear);

    // Find members by partial major match (case-insensitive) - Boy Scout Rule: Descriptive naming  
    @Query("SELECT m FROM Member m WHERE LOWER(m.major) LIKE LOWER(CONCAT('%', :major, '%'))")
    List<Member> findMembersByPartialMajorMatch(@Param("major") String major);

    // Check if email exists - Boy Scout Rule: Clear validation purpose
    boolean existsMemberByEmail(String email);

    // Check if email exists for a different member (for updates) - Boy Scout Rule: Clear update context
    boolean existsMemberByEmailAndIdNot(String email, Long id);

    // Find chapter officers (President, Vice President, Secretary, Treasurer, Officer)
    @Query(MemberQueryConstants.SELECT_MEMBER + " WHERE " + MemberQueryConstants.CHAPTER_MATCH_CONDITION + " AND " + MemberQueryConstants.OFFICER_ROLES_CONDITION + " AND " + MemberQueryConstants.ACTIVE_CONDITION + " " + MemberQueryConstants.ORDER_BY_ROLE)
    List<Member> findActiveChapterOfficers(@Param(MemberQueryConstants.CHAPTER_ID_PARAM) Long chapterId);

    // Search members by multiple criteria within a chapter - Boy Scout Rule: Improved readability
    @Query("""
            SELECT m FROM Member m 
            WHERE m.chapter.id = :chapterId 
            AND (:firstName IS NULL OR LOWER(m.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) 
            AND (:lastName IS NULL OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) 
            AND (:email IS NULL OR LOWER(m.email) LIKE LOWER(CONCAT('%', :email, '%'))) 
            AND (:role IS NULL OR m.role = :role) 
            AND (:major IS NULL OR LOWER(m.major) LIKE LOWER(CONCAT('%', :major, '%'))) 
            AND (:graduationYear IS NULL OR m.graduationYear = :graduationYear) 
            AND (:active IS NULL OR m.active = :active)
            """)
    List<Member> searchMembersByMultipleCriteria(
            @Param(MemberQueryConstants.CHAPTER_ID_PARAM) Long chapterId,
            @Param(MemberQueryConstants.FIRST_NAME_PARAM) String firstName,
            @Param(MemberQueryConstants.LAST_NAME_PARAM) String lastName,
            @Param(MemberQueryConstants.EMAIL_PARAM) String email,
            @Param(MemberQueryConstants.ROLE_PARAM) MemberRole role,
            @Param(MemberQueryConstants.MAJOR_PARAM) String major,
            @Param(MemberQueryConstants.GRADUATION_YEAR_PARAM) String graduationYear,
            @Param(MemberQueryConstants.ACTIVE_PARAM) Boolean active);

    // Count active members by chapter
    @Query(MemberQueryConstants.COUNT_MEMBER + " WHERE " + MemberQueryConstants.CHAPTER_MATCH_CONDITION + " AND " + MemberQueryConstants.ACTIVE_CONDITION)
    Long countActiveMembersByChapterId(@Param(MemberQueryConstants.CHAPTER_ID_PARAM) Long chapterId);

    // Count members by role in a chapter  
    @Query(MemberQueryConstants.COUNT_MEMBER + " WHERE " + MemberQueryConstants.CHAPTER_MATCH_CONDITION + " AND " + MemberQueryConstants.ROLE_MATCH_CONDITION + " AND " + MemberQueryConstants.ACTIVE_CONDITION)
    Long countActiveMembersByChapterIdAndRole(@Param(MemberQueryConstants.CHAPTER_ID_PARAM) Long chapterId, @Param(MemberQueryConstants.ROLE_PARAM) MemberRole role);

    // Find the active president of a chapter
    @Query(MemberQueryConstants.SELECT_MEMBER + " WHERE " + MemberQueryConstants.CHAPTER_MATCH_CONDITION + " AND m.role = 'PRESIDENT' AND " + MemberQueryConstants.ACTIVE_CONDITION)
    Optional<Member> findActiveChapterPresident(@Param(MemberQueryConstants.CHAPTER_ID_PARAM) Long chapterId);

    // Optimized queries with fetch joins to prevent N+1 queries - Boy Scout Rule: Performance optimization
    @Query(MemberQueryConstants.SELECT_DISTINCT_MEMBER + " " + MemberQueryConstants.LEFT_JOIN_CHAPTER + " WHERE " + MemberQueryConstants.ACTIVE_CONDITION)
    List<Member> findAllActiveMembersWithChapterDetails();

    @Query(MemberQueryConstants.SELECT_DISTINCT_MEMBER + " " + MemberQueryConstants.LEFT_JOIN_CHAPTER + " WHERE m.id = :id")
    Optional<Member> findMemberByIdWithChapterDetails(@Param(MemberQueryConstants.MEMBER_ID_PARAM) Long id);

    @Query(MemberQueryConstants.SELECT_DISTINCT_MEMBER + " " + MemberQueryConstants.LEFT_JOIN_CHAPTER + " WHERE m.email = :email")
    Optional<Member> findMemberByEmailWithChapterDetails(@Param(MemberQueryConstants.EMAIL_PARAM) String email);

    @Query(MemberQueryConstants.SELECT_DISTINCT_MEMBER + " " + MemberQueryConstants.LEFT_JOIN_CHAPTER + " WHERE " + MemberQueryConstants.CHAPTER_MATCH_CONDITION + " AND " + MemberQueryConstants.ACTIVE_CONDITION)
    List<Member> findActiveMembersByChapterIdWithChapterDetails(@Param(MemberQueryConstants.CHAPTER_ID_PARAM) Long chapterId);

    // ============= BACKWARD COMPATIBILITY METHODS - Boy Scout Rule: Gradual migration =============
    // These methods maintain backward compatibility while we migrate to better names
    
    /**
     * @deprecated Use findMemberByEmail(String email) instead
     */
    @Deprecated(since = "1.0.0")
    default Optional<Member> findByEmail(String email) {
        return findMemberByEmail(email);
    }
    
    /**
     * @deprecated Use findMemberByUsername(String username) instead  
     */
    @Deprecated(since = "1.0.0")
    default Optional<Member> findByUsername(String username) {
        return findMemberByUsername(username);
    }
    
    /**
     * @deprecated Use findActiveMembersByChapterId(Long chapterId) instead
     */
    @Deprecated(since = "1.0.0")
    default List<Member> findByChapter_IdAndActiveTrue(Long chapterId) {
        return findActiveMembersByChapterId(chapterId);
    }
    
    /**
     * @deprecated Use findAllMembersByChapterId(Long chapterId) instead
     */
    @Deprecated(since = "1.0.0")
    default List<Member> findByChapter_Id(Long chapterId) {
        return findAllMembersByChapterId(chapterId);
    }
    
    /**
     * @deprecated Use findMembersByChapterIdAndRole(Long chapterId, MemberRole role) instead
     */
    @Deprecated(since = "1.0.0")
    default List<Member> findByChapter_IdAndRole(Long chapterId, MemberRole role) {
        return findMembersByChapterIdAndRole(chapterId, role);
    }
    
    /**
     * @deprecated Use findMembersByPartialNameMatch(String firstName, String lastName) instead
     */
    @Deprecated(since = "1.0.0")
    default List<Member> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName) {
        return findMembersByPartialNameMatch(firstName, lastName);
    }
    
    /**
     * @deprecated Use existsMemberByEmail(String email) instead
     */
    @Deprecated(since = "1.0.0")
    default boolean existsByEmail(String email) {
        return existsMemberByEmail(email);
    }
    
    /**
     * @deprecated Use existsMemberByEmailAndIdNot(String email, Long id) instead
     */
    @Deprecated(since = "1.0.0")
    default boolean existsByEmailAndIdNot(String email, Long id) {
        return existsMemberByEmailAndIdNot(email, id);
    }
}