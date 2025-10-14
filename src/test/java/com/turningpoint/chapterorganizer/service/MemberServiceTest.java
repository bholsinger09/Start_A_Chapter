package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChapterService chapterService;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private Chapter testChapter;

    @BeforeEach
    void setUp() {
        testChapter = new Chapter();
        testChapter.setId(1L);
        testChapter.setName("Test Chapter");
        testChapter.setUniversityName("Test University");
        testChapter.setState("California");
        testChapter.setCity("Los Angeles");
        testChapter.setActive(true);

        testMember = new Member();
        testMember.setId(1L);
        testMember.setFirstName("John");
        testMember.setLastName("Doe");
        testMember.setEmail("john.doe@example.com");
        testMember.setRole(MemberRole.MEMBER);
        testMember.setActive(true);
        testMember.setChapter(testChapter);
    }

    @Test
    void createMember_ShouldReturnSavedMember_WhenValidInput() {
        // Given
        Member newMember = new Member("Jane", "Smith", "jane.smith@example.com", testChapter);
        when(memberRepository.existsByEmail(newMember.getEmail())).thenReturn(false);
        when(chapterService.getChapterById(testChapter.getId())).thenReturn(Optional.of(testChapter));
        when(memberRepository.save(any(Member.class))).thenReturn(newMember);

        // When
        Member result = memberService.createMember(newMember);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(result.getActive()).isTrue();
        verify(memberRepository).save(newMember);
    }

    @Test
    void createMember_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        when(memberRepository.existsByEmail(testMember.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> memberService.createMember(testMember))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member with this email already exists");
    }

    @Test
    void createMember_ShouldThrowException_WhenChapterNotFound() {
        // Given
        testMember.getChapter().setId(999L);
        when(memberRepository.existsByEmail(testMember.getEmail())).thenReturn(false);
        when(chapterService.getChapterById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberService.createMember(testMember))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chapter not found with id: 999");
    }

    @Test
    void getMemberById_ShouldReturnMember_WhenExists() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        // When
        Optional<Member> result = memberService.getMemberById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void getMembersByChapter_ShouldReturnActiveMembers() {
        // Given
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findByChapterIdAndActiveTrue(1L)).thenReturn(members);

        // When
        List<Member> result = memberService.getMembersByChapter(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActive()).isTrue();
        assertThat(result.get(0).getChapter().getId()).isEqualTo(1L);
    }

    @Test
    void updateMemberRole_ShouldUpdateRole_WhenValidInput() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // When
        Member result = memberService.updateMemberRole(1L, MemberRole.PRESIDENT);

        // Then
        assertThat(result.getRole()).isEqualTo(MemberRole.PRESIDENT);
        verify(memberRepository).save(testMember);
    }

    @Test
    void updateMemberRole_ShouldThrowException_WhenMemberNotFound() {
        // Given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberService.updateMemberRole(999L, MemberRole.PRESIDENT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member not found with id: 999");
    }

    @Test
    void getMembersByRole_ShouldReturnMembersWithSpecificRole() {
        // Given
        testMember.setRole(MemberRole.PRESIDENT);
        List<Member> presidents = Arrays.asList(testMember);
        when(memberRepository.findByChapterIdAndRoleAndActiveTrue(1L, MemberRole.PRESIDENT)).thenReturn(presidents);

        // When
        List<Member> result = memberService.getMembersByRole(1L, MemberRole.PRESIDENT);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(MemberRole.PRESIDENT);
    }

    @Test
    void getChapterPresident_ShouldReturnPresident_WhenExists() {
        // Given
        testMember.setRole(MemberRole.PRESIDENT);
        when(memberRepository.findChapterPresident(1L)).thenReturn(Optional.of(testMember));

        // When
        Optional<Member> result = memberService.getChapterPresident(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(MemberRole.PRESIDENT);
    }

    @Test
    void deactivateMember_ShouldMarkAsInactive_WhenExists() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // When
        memberService.deactivateMember(1L);

        // Then
        assertThat(testMember.getActive()).isFalse();
        verify(memberRepository).save(testMember);
    }

    @Test
    void countActiveMembers_ShouldReturnCount() {
        // Given
        when(memberRepository.countActiveMembersByChapter(1L)).thenReturn(5L);

        // When
        Long result = memberService.countActiveMembersByChapter(1L);

        // Then
        assertThat(result).isEqualTo(5L);
    }
}