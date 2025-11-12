package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.MemberService;
import com.turningpoint.chapterorganizer.testutil.MemberAssertions;
import com.turningpoint.chapterorganizer.testutil.TestConfiguration;
import com.turningpoint.chapterorganizer.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.turningpoint.chapterorganizer.testutil.MemberAssertions.assertThat;
import static com.turningpoint.chapterorganizer.testutil.TestDataBuilder.aChapter;
import static com.turningpoint.chapterorganizer.testutil.TestDataBuilder.aMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController Integration Tests following JUnit Internals principles
 * 
 * Tests the original AuthController (refactored version is commented out)
 * Validates:
 * - HTTP contract compliance
 * - Input validation using our test data builders
 * - Integration with successive refinement service methods
 * - Error handling with meaningful responses
 */
@WebMvcTest(AuthController.class)
@Tag("integration")
@Tag("web")
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private MemberService memberService;
    
    @MockBean
    private ChapterService chapterService;
    
    private TestConfiguration.TestDataSet testData;
    
    @BeforeEach
    void setUp() {
        testData = TestConfiguration.TestDataSets.complete();
    }

    @Nested
    @DisplayName("Registration Endpoint Tests")
    class RegistrationEndpointTests {

        @Test
        @DisplayName("Should register member successfully with valid input")
        void shouldRegisterMemberSuccessfullyWithValidInput() throws Exception {
            // Given
            Chapter chapter = testData.getFirstChapter();
            Member createdMember = aMember()
                .johnSmith()
                .asPresident()
                .inChapter(chapter)
                .withId(1L)
                .build();
            
            when(chapterService.getChapterById(1L)).thenReturn(Optional.of(chapter));
            when(memberService.createMember(any(Member.class))).thenReturn(createdMember);
            
            String requestBody = """
                {
                    "firstName": "John",
                    "lastName": "Smith",
                    "email": "john.smith@ucla.edu",
                    "username": "johnsmith",
                    "phoneNumber": "310-555-0101",
                    "role": "PRESIDENT",
                    "major": "Computer Science",
                    "graduationYear": "2024",
                    "password": "password123",
                    "chapterId": "1"
                }""";

            // When & Then
            MvcResult result = mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("successfully")))
                .andReturn();
            
            // Validate the response indicates success
            String response = result.getResponse().getContentAsString();
            assertThat(response).contains("John Smith");
            assertThat(response).contains("registered successfully");
        }

        @Test
        @DisplayName("Should handle registration with missing chapter gracefully")
        void shouldHandleRegistrationWithMissingChapterGracefully() throws Exception {
            // Given
            when(chapterService.getChapterById(999L)).thenReturn(Optional.empty());
            
            String requestBody = """
                {
                    "firstName": "John",
                    "lastName": "Smith",
                    "email": "john.smith@test.com",
                    "username": "johnsmith",
                    "phoneNumber": "555-0101",
                    "role": "MEMBER",
                    "major": "Computer Science",
                    "graduationYear": "2024",
                    "password": "password123",
                    "chapterId": "999"
                }""";

            // When & Then
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Chapter not found")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "invalid-email", "missing@"})
        @DisplayName("Should reject invalid email formats")
        void shouldRejectInvalidEmailFormats(String invalidEmail) throws Exception {
            // Given
            String requestBody = String.format("""
                {
                    "firstName": "John",
                    "lastName": "Smith",
                    "email": "%s",
                    "username": "johnsmith",
                    "phoneNumber": "555-0101",
                    "role": "MEMBER",
                    "major": "Computer Science",
                    "graduationYear": "2024",
                    "password": "password123",
                    "chapterId": "1"
                }""", invalidEmail);

            // When & Then
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle service layer exceptions appropriately")
        void shouldHandleServiceLayerExceptionsAppropriately() throws Exception {
            // Given
            Chapter chapter = testData.getFirstChapter();
            when(chapterService.getChapterById(1L)).thenReturn(Optional.of(chapter));
            when(memberService.createMember(any(Member.class)))
                .thenThrow(new RuntimeException("Database error"));
            
            String requestBody = """
                {
                    "firstName": "John",
                    "lastName": "Smith",
                    "email": "john.smith@ucla.edu",
                    "username": "johnsmith",
                    "phoneNumber": "310-555-0101",
                    "role": "PRESIDENT",
                    "major": "Computer Science",
                    "graduationYear": "2024",
                    "password": "password123",
                    "chapterId": "1"
                }""";

            // When & Then
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Registration failed")));
        }
    }

    @Nested
    @DisplayName("Login Endpoint Tests") 
    class LoginEndpointTests {

        @Test
        @DisplayName("Should authenticate valid user credentials")
        void shouldAuthenticateValidUserCredentials() throws Exception {
            // Given
            Member existingMember = aMember()
                .johnSmith()
                .withUsername("johnsmith")
                .withPassword("password123")
                .inChapter(testData.getFirstChapter())
                .withId(1L)
                .build();
            
            when(memberService.getMemberByUsername("johnsmith"))
                .thenReturn(Optional.of(existingMember));
            
            String requestBody = """
                {
                    "username": "johnsmith",
                    "password": "password123"
                }""";

            // When & Then
            MvcResult result = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();
            
            // Validate successful login response
            String response = result.getResponse().getContentAsString();
            assertThat(response).contains("Login successful");
            assertThat(response).contains("johnsmith");
        }

        @Test
        @DisplayName("Should reject invalid credentials")
        void shouldRejectInvalidCredentials() throws Exception {
            // Given
            when(memberService.getMemberByUsername("nonexistent"))
                .thenReturn(Optional.empty());
            
            String requestBody = """
                {
                    "username": "nonexistent",
                    "password": "wrongpassword"
                }""";

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid")));
        }
    }

    @Nested
    @DisplayName("Integration with Successive Refinement Tests")
    class SuccessiveRefinementIntegrationTests {

        @Test
        @DisplayName("Should work with refactored MemberService.createMember method")
        void shouldWorkWithRefactoredMemberServiceCreateMethod() throws Exception {
            // Given - Test that our controller integrates with refactored service
            Chapter chapter = aChapter().ucla().withId(1L).build();
            
            // Mock the refactored createMember method behavior
            when(chapterService.getChapterById(1L)).thenReturn(Optional.of(chapter));
            when(memberService.createMember(any(Member.class))).thenAnswer(invocation -> {
                Member inputMember = invocation.getArgument(0);
                
                // Verify the member passed to service was properly constructed
                assertThat(inputMember).isValidMember();
                
                // Return a properly created member (simulating our refactored service)
                return aMember()
                    .withName(inputMember.getFirstName(), inputMember.getLastName())
                    .withEmail(inputMember.getEmail())
                    .inChapter(chapter)
                    .withId(1L)
                    .build();
            });
            
            String requestBody = """
                {
                    "firstName": "Test",
                    "lastName": "User",
                    "email": "test.user@ucla.edu",
                    "username": "testuser",
                    "phoneNumber": "310-555-9999",
                    "role": "MEMBER",
                    "major": "Testing",
                    "graduationYear": "2024",
                    "password": "testpassword",
                    "chapterId": "1"
                }""";

            // When & Then
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Test User")));
        }
    }
}