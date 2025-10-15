package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for Role entity operations.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);
    
    /**
     * Find roles by names
     */
    List<Role> findByNameIn(Set<String> names);
    
    /**
     * Find all system roles
     */
    List<Role> findByIsSystemRoleTrue();
    
    /**
     * Find all non-system roles
     */
    List<Role> findByIsSystemRoleFalse();
    
    /**
     * Find all assignable roles
     */
    List<Role> findByIsAssignableTrue();
    
    /**
     * Find roles by hierarchy level range
     */
    List<Role> findByHierarchyLevelBetween(Integer minLevel, Integer maxLevel);
    
    /**
     * Find roles with minimum hierarchy level
     */
    List<Role> findByHierarchyLevelGreaterThanEqual(Integer minLevel);
    
    /**
     * Find roles with maximum hierarchy level
     */
    List<Role> findByHierarchyLevelLessThanEqual(Integer maxLevel);
    
    /**
     * Find roles ordered by hierarchy level descending
     */
    @Query("SELECT r FROM Role r ORDER BY r.hierarchyLevel DESC")
    List<Role> findAllOrderByHierarchyLevelDesc();
    
    /**
     * Find roles ordered by hierarchy level ascending
     */
    @Query("SELECT r FROM Role r ORDER BY r.hierarchyLevel ASC")
    List<Role> findAllOrderByHierarchyLevelAsc();
    
    /**
     * Check if role exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Find roles with specific permission
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
    List<Role> findRolesWithPermission(@Param("permissionName") String permissionName);
    
    /**
     * Find roles with specific resource and action permission
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.permissions p WHERE p.resource = :resource AND p.action = :action")
    List<Role> findRolesWithResourceAction(@Param("resource") String resource, @Param("action") String action);
    
    /**
     * Find roles with any of the specified permissions
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.permissions p WHERE p.name IN :permissionNames")
    List<Role> findRolesWithAnyPermission(@Param("permissionNames") Set<String> permissionNames);
    
    /**
     * Find roles without specific permission
     */
    @Query("SELECT r FROM Role r WHERE r.id NOT IN (SELECT DISTINCT r2.id FROM Role r2 JOIN r2.permissions p WHERE p.name = :permissionName)")
    List<Role> findRolesWithoutPermission(@Param("permissionName") String permissionName);
    
    /**
     * Count roles by hierarchy level
     */
    Long countByHierarchyLevel(Integer hierarchyLevel);
    
    /**
     * Find roles assigned to users
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.userRoles ur WHERE ur.isActive = true")
    List<Role> findRolesInUse();
    
    /**
     * Find roles not assigned to any users
     */
    @Query("SELECT r FROM Role r WHERE r.id NOT IN (SELECT DISTINCT r2.id FROM Role r2 JOIN r2.userRoles ur WHERE ur.isActive = true)")
    List<Role> findUnassignedRoles();
    
    /**
     * Find roles by name pattern
     */
    @Query("SELECT r FROM Role r WHERE r.name LIKE %:pattern%")
    List<Role> findByNameContaining(@Param("pattern") String pattern);
}