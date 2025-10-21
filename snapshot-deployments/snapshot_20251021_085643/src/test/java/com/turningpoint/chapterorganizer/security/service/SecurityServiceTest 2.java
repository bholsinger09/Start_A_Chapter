package com.turningpoint.chapterorganizer.security.service;

import com.turningpoint.chapterorganizer.entity.*;
import com.turningpoint.chapterorganizer.repository.UserRoleRepository;
import com.turningpoint.chapterorganizer.security.context.SecurityContext;
import com.turningpoint.chapterorganizer.security.context.SecurityContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/**
 * Unit tests for SecurityService RBAC functionality.
 */
@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {
    
    @Mock
    private UserRoleRepository userRoleRepository;
    
    @InjectMocks
    private SecurityService securityService;
    
    private Member testUser;
    private Chapter testChapter;
    private Role adminRole;
    private Role memberRole;
    private Permission readPermission;
    private Permission writePermission;
    
    @BeforeEach
    void setUp() {
        // Clear security context
        SecurityContextHolder.clearContext();
        
        // Create test entities
        testUser = new Member();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        
        testChapter = new Chapter();
        testChapter.setId(1L);
        testChapter.setName("Test Chapter");
        
        // Create permissions
        readPermission = new Permission("chapter:read", "chapter", "read", "Read chapter information");
        readPermission.setId(1L);
        
        writePermission = new Permission("chapter:write", "chapter", "write", "Write chapter information");
        writePermission.setId(2L);
        
        // Create roles
        adminRole = new Role("Chapter Administrator", "Chapter admin role", 70);
        adminRole.setId(1L);
        adminRole.addPermission(readPermission);
        adminRole.addPermission(writePermission);
        
        memberRole = new Role("Chapter Member", "Chapter member role", 20);
        memberRole.setId(2L);
        memberRole.addPermission(readPermission);
    }
    
    @Test
    void testInitializeSecurityContext() {
        // Arrange
        UserRole userRole = new UserRole(testUser, adminRole, testChapter);
        userRole.setId(1L);
        when(userRoleRepository.findByUserIdAndIsActiveTrue(1L))
                .thenReturn(Arrays.asList(userRole));
        
        // Act
        securityService.initializeSecurityContext(testUser);
        
        // Assert
        assertTrue(SecurityContextHolder.hasContext());
        SecurityContext context = SecurityContextHolder.getContext();
        assertNotNull(context);
        assertEquals(testUser, context.getCurrentUser());
        assertTrue(context.hasPermission("chapter", "read"));
        assertTrue(context.hasPermission("chapter", "write"));
        assertTrue(context.hasRole("Chapter Administrator"));
    }
    
    @Test
    void testHasPermission() {
        // Arrange
        UserRole userRole = new UserRole(testUser, adminRole, testChapter);
        SecurityContext context = new SecurityContext(testUser, Set.of(userRole));
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertTrue(securityService.hasPermission("chapter", "read"));
        assertTrue(securityService.hasPermission("chapter", "write"));
        assertFalse(securityService.hasPermission("event", "create"));
    }
    
    @Test
    void testHasRole() {
        // Arrange
        UserRole userRole = new UserRole(testUser, adminRole, testChapter);
        SecurityContext context = new SecurityContext(testUser, Set.of(userRole));
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertTrue(securityService.hasRole("Chapter Administrator"));
        assertFalse(securityService.hasRole("Super Administrator"));
    }
    
    @Test
    void testHasMinimumHierarchyLevel() {
        // Arrange
        UserRole userRole = new UserRole(testUser, adminRole, testChapter);
        SecurityContext context = new SecurityContext(testUser, Set.of(userRole));
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertTrue(securityService.hasMinimumHierarchyLevel(50));
        assertTrue(securityService.hasMinimumHierarchyLevel(70));
        assertFalse(securityService.hasMinimumHierarchyLevel(80));
    }
    
    @Test
    void testChapterScopedPermissions() {
        // Arrange
        UserRole chapterUserRole = new UserRole(testUser, adminRole, testChapter);
        SecurityContext context = new SecurityContext(testUser, Set.of(chapterUserRole));
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertTrue(securityService.hasChapterPermission(1L, "chapter", "read"));
        assertFalse(securityService.hasChapterPermission(2L, "chapter", "read")); // Different chapter
    }
    
    @Test
    void testGlobalVsChapterRoles() {
        // Arrange
        UserRole globalRole = new UserRole(testUser, adminRole); // No chapter = global
        UserRole chapterRole = new UserRole(testUser, memberRole, testChapter);
        
        SecurityContext context = new SecurityContext(testUser, Set.of(globalRole, chapterRole));
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertTrue(securityService.hasRole("Chapter Administrator"));
        assertTrue(securityService.hasRole("Chapter Member"));
        assertTrue(securityService.hasChapterRole(1L, "Chapter Member"));
        assertFalse(securityService.hasChapterRole(2L, "Chapter Member"));
    }
    
    @Test
    void testValidatePermissionLogic() {
        // Arrange
        UserRole userRole = new UserRole(testUser, adminRole, testChapter);
        SecurityContext context = new SecurityContext(testUser, Set.of(userRole));
        SecurityContextHolder.setContext(context);
        
        String[] permissions = {"chapter:read", "chapter:write"};
        
        // Act & Assert - Require ALL permissions
        assertTrue(securityService.validatePermission(permissions, true, false, null));
        
        // Act & Assert - Require ANY permission
        assertTrue(securityService.validatePermission(new String[]{"chapter:read", "event:create"}, false, false, null));
        assertFalse(securityService.validatePermission(new String[]{"event:create", "event:delete"}, false, false, null));
    }
    
    @Test
    void testExpiredUserRoles() {
        // Arrange
        UserRole expiredRole = new UserRole(testUser, adminRole, testChapter);
        expiredRole.setExpiresAt(LocalDateTime.now().minusDays(1)); // Expired yesterday
        
        SecurityContext context = new SecurityContext(testUser, Set.of(expiredRole));
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertFalse(securityService.hasRole("Chapter Administrator"));
        assertFalse(securityService.hasPermission("chapter", "read"));
    }
    
    @Test
    void testRevokedUserRoles() {
        // Arrange
        UserRole revokedRole = new UserRole(testUser, adminRole, testChapter);
        revokedRole.revoke(testUser, "Test revocation");
        
        SecurityContext context = new SecurityContext(testUser, Set.of(revokedRole));
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertFalse(securityService.hasRole("Chapter Administrator"));
        assertFalse(securityService.hasPermission("chapter", "read"));
    }
    
    @Test
    void testNoSecurityContext() {
        // Arrange
        SecurityContextHolder.clearContext();
        
        // Act & Assert
        assertFalse(securityService.hasPermission("chapter", "read"));
        assertFalse(securityService.hasRole("Chapter Administrator"));
        assertFalse(securityService.hasMinimumHierarchyLevel(10));
    }
    
    @Test
    void testMultipleRolesCombined() {
        // Arrange
        UserRole adminUserRole = new UserRole(testUser, this.adminRole, testChapter);
        UserRole memberUserRole = new UserRole(testUser, this.memberRole, testChapter);
        
        SecurityContext context = new SecurityContext(testUser, Set.of(adminUserRole, memberUserRole));
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertTrue(securityService.hasRole("Chapter Administrator"));
        assertTrue(securityService.hasRole("Chapter Member"));
        assertTrue(securityService.hasAnyRole("Chapter Administrator", "Super Administrator"));
        assertTrue(securityService.hasAllRoles("Chapter Administrator", "Chapter Member"));
        assertFalse(securityService.hasAllRoles("Chapter Administrator", "Super Administrator"));
    }
    
    @Test
    void testSecuritySummary() {
        // Arrange
        UserRole userRole = new UserRole(testUser, adminRole, testChapter);
        SecurityContext context = new SecurityContext(testUser, Set.of(userRole));
        SecurityContextHolder.setContext(context);
        
        // Act
        SecurityService.SecuritySummary summary = securityService.getCurrentUserSecuritySummary();
        
        // Assert
        assertNotNull(summary);
        assertEquals(1L, summary.getUserId());
        assertEquals("test@example.com", summary.getUserEmail());
        assertTrue(summary.getRoles().contains("Chapter Administrator"));
        assertTrue(summary.getPermissions().contains("chapter:read"));
        assertTrue(summary.getPermissions().contains("chapter:write"));
        assertEquals(70, summary.getHighestHierarchyLevel());
        assertFalse(summary.isSystemAdmin());
    }
    
    @Test
    void testResourceOwnership() {
        // Arrange
        SecurityContext context = new SecurityContext(testUser, Set.of());
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertTrue(securityService.isResourceOwner(1L)); // Same as testUser.getId()
        assertFalse(securityService.isResourceOwner(2L)); // Different user ID
    }
    
    @Test
    void testSystemAdminDetection() {
        // Arrange
        Role superAdminRole = new Role("Super Administrator", "System super admin", 100);
        UserRole userRole = new UserRole(testUser, superAdminRole); // Global role
        
        SecurityContext context = new SecurityContext(testUser, Set.of(userRole));
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertTrue(securityService.isSystemAdmin());
    }
    
    @Test
    void testChapterAdminDetection() {
        // Arrange
        Role presidentRole = new Role("Chapter President", "Chapter president role", 65);
        UserRole userRole = new UserRole(testUser, presidentRole, testChapter);
        
        SecurityContext context = new SecurityContext(testUser, Set.of(userRole));
        SecurityContextHolder.setContext(context);
        
        // Act & Assert
        assertTrue(securityService.isChapterAdmin(1L)); // Same chapter
        assertFalse(securityService.isChapterAdmin(2L)); // Different chapter
    }
}