package com.turningpoint.chapterorganizer.mapper;

import com.turningpoint.chapterorganizer.dto.MemberDTO;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MemberMapper.
 * Tests DTO conversion with proper validation and edge cases.
 */
@ExtendWith(MockitoExtension.class)
class MemberMapperTest {

    @InjectMocks
    private MemberMapper memberMapper;

    private Member testMember;
    private MemberDTO testMemberDTO;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setId(1L);
        testMember.setFirstName("John");
        testMember.setLastName("Doe");
        testMember.setEmail("john.doe@example.com");
        testMember.setPhoneNumber("555-1234");
        testMember.setMajor("Computer Science");
        testMember.setGraduationYear("2024");
        testMember.setRole(MemberRole.MEMBER);
        testMember.setActive(true);

        testMemberDTO = new MemberDTO(
                1L, "John", "Doe", "john.doe@example.com", "johndoe",
                "555-1234", MemberRole.MEMBER, true, "Computer Science", 
                "2024", 1L, "Test Chapter", "Test University", 
                "Test State", "Test City", null, null
        );
    }

    @Test
    void toDTO_WithValidMember_ShouldConvertCorrectly() {
        // When
        MemberDTO result = memberMapper.toDTO(testMember);

        // Then
        assertNotNull(result);
        assertEquals(testMember.getId(), result.getId());
        assertEquals(testMember.getFirstName(), result.getFirstName());
        assertEquals(testMember.getLastName(), result.getLastName());
        assertEquals(testMember.getEmail(), result.getEmail());
        assertEquals(testMember.getRole(), result.getRole());
    }

    @Test
    void toDTO_WithNullMember_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> memberMapper.toDTO(null)
        );
        assertEquals("Member cannot be null", exception.getMessage());
    }

    @Test
    void toDTOList_WithValidMembers_ShouldConvertAll() {
        // Given
        Member member2 = new Member();
        member2.setId(2L);
        member2.setFirstName("Jane");
        member2.setLastName("Smith");
        member2.setEmail("jane.smith@example.com");
        
        List<Member> members = Arrays.asList(testMember, member2);

        // When
        List<MemberDTO> result = memberMapper.toDTOList(members);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testMember.getId(), result.get(0).getId());
        assertEquals(member2.getId(), result.get(1).getId());
    }

    @Test
    void toDTOList_WithNullMember_ShouldSkipNull() {
        // Given
        List<Member> members = Arrays.asList(testMember, null, new Member());

        // When
        List<MemberDTO> result = memberMapper.toDTOList(members);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Should skip the null member
    }

    @Test
    void toDTOList_WithNullList_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> memberMapper.toDTOList(null)
        );
        assertEquals("Members list cannot be null", exception.getMessage());
    }

    @Test
    void updateFromDTO_WithValidData_ShouldUpdateFields() {
        // Given
        Member member = new Member();
        member.setFirstName("Original");
        
        MemberDTO dto = new MemberDTO(
                null, "Updated", "Name", "updated@example.com", null,
                null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When
        memberMapper.updateFromDTO(member, dto);

        // Then
        assertEquals("Updated", member.getFirstName());
        assertEquals("Name", member.getLastName());
        assertEquals("updated@example.com", member.getEmail());
    }

    @Test
    void updateFromDTO_WithNullMember_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> memberMapper.updateFromDTO(null, testMemberDTO)
        );
        assertEquals("Member cannot be null", exception.getMessage());
    }

    @Test
    void updateFromDTO_WithNullDTO_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> memberMapper.updateFromDTO(testMember, null)
        );
        assertEquals("MemberDTO cannot be null", exception.getMessage());
    }

    @Test
    void fromDTO_WithValidDTO_ShouldCreateMember() {
        // When
        Member result = memberMapper.fromDTO(testMemberDTO);

        // Then
        assertNotNull(result);
        assertEquals(testMemberDTO.getFirstName(), result.getFirstName());
        assertEquals(testMemberDTO.getLastName(), result.getLastName());
        assertEquals(testMemberDTO.getEmail(), result.getEmail());
        assertTrue(result.getActive()); // Should default to true
    }

    @Test
    void fromDTO_WithMissingFirstName_ShouldThrowException() {
        // Given
        MemberDTO dto = new MemberDTO(
                null, null, "Doe", "john.doe@example.com", null,
                null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> memberMapper.fromDTO(dto)
        );
        assertEquals("First name is required", exception.getMessage());
    }

    @Test
    void fromDTO_WithMissingLastName_ShouldThrowException() {
        // Given
        MemberDTO dto = new MemberDTO(
                null, "John", null, "john.doe@example.com", null,
                null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> memberMapper.fromDTO(dto)
        );
        assertEquals("Last name is required", exception.getMessage());
    }

    @Test
    void fromDTO_WithMissingEmail_ShouldThrowException() {
        // Given
        MemberDTO dto = new MemberDTO(
                null, "John", "Doe", null, null,
                null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> memberMapper.fromDTO(dto)
        );
        assertEquals("Email is required", exception.getMessage());
    }
}