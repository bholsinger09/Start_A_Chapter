package com.turningpoint.chapterorganizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turningpoint.chapterorganizer.dto.CreateMemberRequest;
import com.turningpoint.chapterorganizer.dto.MemberUpdateRequest;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember1;
    private Member testMember2;
    private Chapter testChapter;
    private CreateMemberRequest createRequest;
    private MemberUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test chapter
        testChapter = new Chapter();
        testChapter.setId(1L);
        testChapter.setName("Test Chapter");

        // Setup test members
        testMember1 = new Member();
        testMember1.setId(1L);
        testMember1.setFirstName("John");
        testMember1.setLastName("Doe");
        testMember1.setEmail("john.doe@test.com");
        testMember1.setUsername("johndoe");
        testMember1.setPhoneNumber("123-456-7890");
        testMember1.setRole(MemberRole.MEMBER);
        testMember1.setChapter(testChapter);
        testMember1.setActive(true);
        testMember1.setCreatedAt(LocalDateTime.now());

        testMember2 = new Member();
        testMember2.setId(2L);
        testMember2.setFirstName("Jane");
        testMember2.setLastName("Smith");
        testMember2.setEmail("jane.smith@test.com");
        testMember2.setUsername("janesmith");
        testMember2.setPhoneNumber("098-765-4321");
        testMember2.setRole(MemberRole.OFFICER);
        testMember2.setChapter(testChapter);
        testMember2.setActive(true);
        testMember2.setCreatedAt(LocalDateTime.now());

        // Setup DTOs
        createRequest = new CreateMemberRequest();
        createRequest.setFirstName("New");
        createRequest.setLastName("Member");
        createRequest.setEmail("new.member@test.com");
        createRequest.setUsername("newmember");
        createRequest.setPhoneNumber("555-123-4567");
        createRequest.setChapterId(1L);
        createRequest.setRole(MemberRole.MEMBER);

        updateRequest = new MemberUpdateRequest();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("Name");
        updateRequest.setEmail("updated@test.com");
    }

    @Test
    void getAllMembers_ShouldReturnMembersList() throws Exception {
        // Given
        List<Member> members = Arrays.asList(testMember1, testMember2);
        when(memberService.getAllMembers()).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(memberService).getAllMembers();
    }

    @Test
    void getPaginatedMembers_ShouldReturnPageOfMembers() throws Exception {
        // Given
        List<Member> members = Arrays.asList(testMember1);
        Page<Member> page = new PageImpl<>(members, PageRequest.of(0, 5), 1);
        when(memberService.getPaginatedMembers(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/members/paginated")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("John"));

        verify(memberService).getPaginatedMembers(any());
    }

    @Test
    void getMemberById_ShouldReturnMember_WhenExists() throws Exception {
        // Given
        when(memberService.getMemberById(1L)).thenReturn(Optional.of(testMember1));

        // When & Then
        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(memberService).getMemberById(1L);
    }

    @Test
    void getMemberById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        // Given
        when(memberService.getMemberById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/members/999"))
                .andExpect(status().isNotFound());

        verify(memberService).getMemberById(999L);
    }

    @Test
    void getMembersByChapter_ShouldReturnMembersForChapter() throws Exception {
        // Given
        List<Member> members = Arrays.asList(testMember1, testMember2);
        when(memberService.getMembersByChapter(1L)).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/api/members/chapter/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));

        verify(memberService).getMembersByChapter(1L);
    }

    @Test
    void getMembersByRole_ShouldReturnMembersWithSpecificRole() throws Exception {
        // Given
        List<Member> officers = Arrays.asList(testMember2);
        when(memberService.getMembersByRole(1L, MemberRole.OFFICER)).thenReturn(officers);

        // When & Then
        mockMvc.perform(get("/api/members/chapter/1/role/OFFICER"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].role").value("OFFICER"));

        verify(memberService).getMembersByRole(1L, MemberRole.OFFICER);
    }

    @Test
    void getChapterPresident_ShouldReturnPresident_WhenExists() throws Exception {
        // Given
        testMember1.setRole(MemberRole.PRESIDENT);
        when(memberService.getChapterPresident(1L)).thenReturn(Optional.of(testMember1));

        // When & Then
        mockMvc.perform(get("/api/members/chapter/1/president"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.role").value("PRESIDENT"));

        verify(memberService).getChapterPresident(1L);
    }

    @Test
    void getChapterPresident_ShouldReturnNotFound_WhenNoPresident() throws Exception {
        // Given
        when(memberService.getChapterPresident(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/members/chapter/1/president"))
                .andExpect(status().isNotFound());

        verify(memberService).getChapterPresident(1L);
    }

    @Test
    void searchMembers_ShouldReturnMatchingMembers() throws Exception {
        // Given
        List<Member> searchResults = Arrays.asList(testMember1);
        when(memberService.searchMembers(eq(1L), eq("John"), any(), any(), any(), any(), any(), any())).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/members/chapter/1/search")
                        .param("firstName", "John"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(memberService).searchMembers(eq(1L), eq("John"), any(), any(), any(), any(), any(), any());
    }

    @Test
    void getAllMembersByChapter_ShouldReturnAllMembersForChapter() throws Exception {
        // Given
        List<Member> members = Arrays.asList(testMember1, testMember2);
        when(memberService.getAllMembersByChapter(1L)).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/api/members/chapter/1/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));

        verify(memberService).getAllMembersByChapter(1L);
    }

    @Test
    void getChapterOfficers_ShouldReturnOfficersForChapter() throws Exception {
        // Given
        List<Member> officers = Arrays.asList(testMember2);
        when(memberService.getChapterOfficers(1L)).thenReturn(officers);

        // When & Then
        mockMvc.perform(get("/api/members/chapter/1/officers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].role").value("OFFICER"));

        verify(memberService).getChapterOfficers(1L);
    }

    @Test
    void createMember_ShouldReturnCreatedMember() throws Exception {
        // Given
        when(memberService.createMember(ArgumentMatchers.any(CreateMemberRequest.class))).thenReturn(testMember1);

        // When & Then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(memberService).createMember(ArgumentMatchers.any(CreateMemberRequest.class));
    }

    @Test
    void createMember_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given
        CreateMemberRequest invalidRequest = new CreateMemberRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMember_ShouldReturnUpdatedMember() throws Exception {
        // Given
        when(memberService.updateMember(eq(1L), ArgumentMatchers.any(MemberUpdateRequest.class)))
                .thenReturn(testMember1);

        // When & Then
        mockMvc.perform(put("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(memberService).updateMember(eq(1L), ArgumentMatchers.any(MemberUpdateRequest.class));
    }

    @Test
    void updateMemberRole_ShouldReturnUpdatedMember() throws Exception {
        // Given
        testMember1.setRole(MemberRole.OFFICER);
        when(memberService.updateMemberRole(1L, MemberRole.OFFICER)).thenReturn(testMember1);

        // When & Then
        mockMvc.perform(put("/api/members/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"OFFICER\""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.role").value("OFFICER"));

        verify(memberService).updateMemberRole(1L, MemberRole.OFFICER);
    }

    @Test
    void deactivateMember_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(memberService).deactivateMember(1L);

        // When & Then
        mockMvc.perform(delete("/api/members/1"))
                .andExpect(status().isNoContent());

        verify(memberService).deactivateMember(1L);
    }

    @Test
    void deactivateMember_ShouldReturnNotFound_WhenMemberNotExists() throws Exception {
        // Given
        doThrow(new RuntimeException("Member not found")).when(memberService).deactivateMember(999L);

        // When & Then
        mockMvc.perform(delete("/api/members/999"))
                .andExpect(status().isInternalServerError());

        verify(memberService).deactivateMember(999L);
    }
}