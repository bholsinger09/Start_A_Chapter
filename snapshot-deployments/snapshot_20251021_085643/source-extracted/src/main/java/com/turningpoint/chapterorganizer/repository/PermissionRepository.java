package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for Permission entity operations.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * Find permission by name
     */
    Optional<Permission> findByName(String name);
    
    /**
     * Find permissions by resource
     */
    List<Permission> findByResource(String resource);
    
    /**
     * Find permissions by resource and action
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);
    
    /**
     * Find all permissions for a specific resource with actions
     */
    List<Permission> findByResourceAndActionIn(String resource, Set<String> actions);
    
    /**
     * Find all system permissions
     */
    List<Permission> findByIsSystemPermissionTrue();
    
    /**
     * Find all non-system permissions
     */
    List<Permission> findByIsSystemPermissionFalse();
    
    /**
     * Find permissions by name pattern
     */
    @Query("SELECT p FROM Permission p WHERE p.name LIKE %:pattern%")
    List<Permission> findByNameContaining(@Param("pattern") String pattern);
    
    /**
     * Find permissions by multiple names
     */
    List<Permission> findByNameIn(Set<String> names);
    
    /**
     * Check if permission exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Check if permission exists by resource and action
     */
    boolean existsByResourceAndAction(String resource, String action);
    
    /**
     * Find all unique resources
     */
    @Query("SELECT DISTINCT p.resource FROM Permission p ORDER BY p.resource")
    List<String> findAllResources();
    
    /**
     * Find all unique actions
     */
    @Query("SELECT DISTINCT p.action FROM Permission p ORDER BY p.action")
    List<String> findAllActions();
    
    /**
     * Find actions for a specific resource
     */
    @Query("SELECT DISTINCT p.action FROM Permission p WHERE p.resource = :resource ORDER BY p.action")
    List<String> findActionsByResource(@Param("resource") String resource);
    
    /**
     * Count permissions by resource
     */
    @Query("SELECT COUNT(p) FROM Permission p WHERE p.resource = :resource")
    Long countByResource(@Param("resource") String resource);
    
    /**
     * Find permissions used by roles
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r WHERE r.id IN :roleIds")
    List<Permission> findPermissionsByRoleIds(@Param("roleIds") Set<Long> roleIds);
}