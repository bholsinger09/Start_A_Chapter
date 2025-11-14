package com.turningpoint.chapterorganizer.mapper;

import com.turningpoint.chapterorganizer.dto.MemberDTO;
import com.turningpoint.chapterorganizer.entity.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Focused mapper for Member entity to MemberDTO conversion.
 * Provides clean abstraction for data transfer with validation and null safety.
 */
@Component
public class MemberMapper {

    /**
     * Convert a Member entity to MemberDTO
     * @param member the member entity
     * @return MemberDTO with all relevant fields mapped
     * @throws IllegalArgumentException if member is null
     */
    public MemberDTO toDTO(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        
        return MemberDTO.from(member);
    }
    
    /**
     * Convert a list of Member entities to MemberDTOs
     * @param members the list of member entities
     * @return List of MemberDTOs
     * @throws IllegalArgumentException if members list is null
     */
    public List<MemberDTO> toDTOList(List<Member> members) {
        if (members == null) {
            throw new IllegalArgumentException("Members list cannot be null");
        }
        
        return members.stream()
                .filter(member -> member != null)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Update a Member entity from MemberDTO data
     * @param member the existing member entity to update
     * @param memberDTO the DTO containing updated data
     * @throws IllegalArgumentException if either parameter is null
     */
    public void updateFromDTO(Member member, MemberDTO memberDTO) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        if (memberDTO == null) {
            throw new IllegalArgumentException("MemberDTO cannot be null");
        }
        
        // Update only the fields that should be modifiable
        if (memberDTO.getFirstName() != null) {
            member.setFirstName(memberDTO.getFirstName());
        }
        if (memberDTO.getLastName() != null) {
            member.setLastName(memberDTO.getLastName());
        }
        if (memberDTO.getEmail() != null) {
            member.setEmail(memberDTO.getEmail());
        }
        if (memberDTO.getPhoneNumber() != null) {
            member.setPhoneNumber(memberDTO.getPhoneNumber());
        }
        if (memberDTO.getMajor() != null) {
            member.setMajor(memberDTO.getMajor());
        }
        if (memberDTO.getGraduationYear() != null) {
            member.setGraduationYear(memberDTO.getGraduationYear());
        }
        if (memberDTO.getRole() != null) {
            member.setRole(memberDTO.getRole());
        }
        // Note: ID, createdAt, updatedAt, and chapterId are not updated from DTO
        // as they have special handling requirements
    }
    
    /**
     * Create a new Member entity from MemberDTO
     * @param memberDTO the DTO containing member data
     * @return new Member entity
     * @throws IllegalArgumentException if memberDTO is null or missing required fields
     */
    public Member fromDTO(MemberDTO memberDTO) {
        if (memberDTO == null) {
            throw new IllegalArgumentException("MemberDTO cannot be null");
        }
        
        validateRequiredFields(memberDTO);
        
        Member member = new Member();
        updateFromDTO(member, memberDTO);
        
        // Set default values
        if (member.getActive() == null) {
            member.setActive(true);
        }
        
        return member;
    }
    
    private void validateRequiredFields(MemberDTO memberDTO) {
        if (memberDTO.getFirstName() == null || memberDTO.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (memberDTO.getLastName() == null || memberDTO.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (memberDTO.getEmail() == null || memberDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}