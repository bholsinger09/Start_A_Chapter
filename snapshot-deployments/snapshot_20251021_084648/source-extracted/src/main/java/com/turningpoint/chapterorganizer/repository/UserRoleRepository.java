package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for UserRole entity operations.
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    /**
     * Find all active user roles for a user
     */
    List<UserRole> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Find all user roles for a user (active and inactive)
     */
    List<UserRole> findByUserId(Long userId);
    
    /**
     * Find active user roles for a user in a specific chapter
     */
    List<UserRole> findByUserIdAndChapterIdAndIsActiveTrue(Long userId, Long chapterId);
    
    /**
     * Find active global user roles for a user (chapter is null)
     */
    List<UserRole> findByUserIdAndChapterIsNullAndIsActiveTrue(Long userId);
    
    /**
     * Find user role by user, role, and chapter
     */
    Optional<UserRole> findByUserIdAndRoleIdAndChapterId(Long userId, Long roleId, Long chapterId);
    
    /**
     * Find active user role by user, role, and chapter
     */
    Optional<UserRole> findByUserIdAndRoleIdAndChapterIdAndIsActiveTrue(Long userId, Long roleId, Long chapterId);
    
    /**
     * Find users with a specific role
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.role.name = :roleName AND ur.isActive = true")
    List<UserRole> findUsersWithRole(@Param("roleName") String roleName);
    
    /**
     * Find users with a specific role in a chapter
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.role.name = :roleName AND ur.chapter.id = :chapterId AND ur.isActive = true")
    List<UserRole> findUsersWithRoleInChapter(@Param("roleName") String roleName, @Param("chapterId") Long chapterId);
    
    /**
     * Find users with any of the specified roles
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.role.name IN :roleNames AND ur.isActive = true")
    List<UserRole> findUsersWithAnyRole(@Param("roleNames") Set<String> roleNames);
    
    /**
     * Find users with minimum hierarchy level
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.role.hierarchyLevel >= :minLevel AND ur.isActive = true")
    List<UserRole> findUsersWithMinimumHierarchy(@Param("minLevel") Integer minLevel);
    
    /**
     * Find expired user roles
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.expiresAt IS NOT NULL AND ur.expiresAt <= :currentTime AND ur.isActive = true")
    List<UserRole> findExpiredUserRoles(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find expiring user roles (expiring within specified days)
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.expiresAt IS NOT NULL AND ur.expiresAt <= :expirationDate AND ur.expiresAt > :currentTime AND ur.isActive = true")
    List<UserRole> findExpiringUserRoles(@Param("currentTime") LocalDateTime currentTime, @Param("expirationDate") LocalDateTime expirationDate);
    
    /**
     * Find revoked user roles
     */
    List<UserRole> findByRevokedAtIsNotNull();
    
    /**
     * Find user roles granted by a specific user
     */
    List<UserRole> findByGrantedById(Long grantedById);
    
    /**
     * Find user roles revoked by a specific user
     */
    List<UserRole> findByRevokedById(Long revokedById);
    
    /**
     * Check if user has active role
     */
    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.name = :roleName AND ur.isActive = true AND (ur.expiresAt IS NULL OR ur.expiresAt > :currentTime) AND ur.revokedAt IS NULL")
    boolean userHasActiveRole(@Param("userId") Long userId, @Param("roleName") String roleName, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Check if user has active role in chapter
     */
    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.name = :roleName AND ur.chapter.id = :chapterId AND ur.isActive = true AND (ur.expiresAt IS NULL OR ur.expiresAt > :currentTime) AND ur.revokedAt IS NULL")
    boolean userHasActiveRoleInChapter(@Param("userId") Long userId, @Param("roleName") String roleName, @Param("chapterId") Long chapterId, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find all user roles for a chapter
     */
    List<UserRole> findByChapterIdAndIsActiveTrue(Long chapterId);
    
    /**
     * Find chapter administrators
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.chapter.id = :chapterId AND ur.role.name IN ('Chapter President', 'Chapter Vice President') AND ur.isActive = true")
    List<UserRole> findChapterAdministrators(@Param("chapterId") Long chapterId);
    
    /**
     * Find system administrators
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.role.name IN ('Super Administrator', 'System Administrator') AND ur.chapter IS NULL AND ur.isActive = true")
    List<UserRole> findSystemAdministrators();
    
    /**
     * Count active user roles for a user
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.user.id = :userId AND ur.isActive = true AND (ur.expiresAt IS NULL OR ur.expiresAt > :currentTime) AND ur.revokedAt IS NULL")
    Long countActiveUserRoles(@Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Count users with specific role
     */
    @Query("SELECT COUNT(DISTINCT ur.user.id) FROM UserRole ur WHERE ur.role.name = :roleName AND ur.isActive = true")
    Long countUsersWithRole(@Param("roleName") String roleName);
    
    /**
     * Find user roles that need attention (expired, expiring, etc.)
     */
    @Query("SELECT ur FROM UserRole ur WHERE (ur.expiresAt IS NOT NULL AND ur.expiresAt <= :warningDate) OR ur.revokedAt IS NOT NULL")
    List<UserRole> findUserRolesNeedingAttention(@Param("warningDate") LocalDateTime warningDate);
    
    /**
     * Delete expired user roles
     */
    @Query("DELETE FROM UserRole ur WHERE ur.expiresAt IS NOT NULL AND ur.expiresAt <= :cutoffDate")
    void deleteExpiredUserRoles(@Param("cutoffDate") LocalDateTime cutoffDate);
}