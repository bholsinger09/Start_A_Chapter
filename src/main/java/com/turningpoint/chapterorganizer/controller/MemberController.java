package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.dto.MemberDTO;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.service.MemberService;
import com.turningpoint.chapterorganizer.util.ControllerUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(
    origins = {"https://startachapter.duckdns.org", "http://startachapter.duckdns.org", "*"}, 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "false"
)
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Get member by username (treating username as email for compatibility)
     * This endpoint is needed for blog functionality
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<MemberDTO> getMemberByUsername(@PathVariable String username) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            // In our system, username is treated as email
            Optional<Member> member = memberService.getMemberByEmail(username);
            
            return member.isPresent() 
                ? ControllerUtils.ok(convertToDTO(member.get()))
                : ControllerUtils.notFound();
        });
    }

    /**
     * Get all members
     */
    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        try {
            // For now, get all members from all chapters
            // In a real system, you might want to filter by user's chapter
            List<Member> members = memberService.getAllMembers();
            
            // Convert to DTO to avoid circular reference issues
            List<MemberDTO> memberDTOs = members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(memberDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Helper method to convert Member entity to immutable DTO
     * Uses factory method to create thread-safe immutable object
     */
    private MemberDTO convertToDTO(Member member) {
        return MemberDTO.from(member);
    }

    /**
     * Get member by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        try {
            Optional<Member> member = memberService.getMemberById(id);
            
            if (member.isPresent()) {
                return ResponseEntity.ok(convertToDTO(member.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get member by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<MemberDTO> getMemberByEmail(@PathVariable String email) {
        try {
            Optional<Member> member = memberService.getMemberByEmail(email);
            
            if (member.isPresent()) {
                return ResponseEntity.ok(convertToDTO(member.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create new member
     */
    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody Member member) {
        try {
            Member createdMember = memberService.createMember(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update member
     */
    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody Member member) {
        try {
            Member updatedMember = memberService.updateMember(id, member);
            return ResponseEntity.ok(convertToDTO(updatedMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get members by chapter
     */
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<MemberDTO>> getMembersByChapter(@PathVariable Long chapterId) {
        try {
            List<Member> members = memberService.getMembersByChapter(chapterId);
            return ResponseEntity.ok(members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search members by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<MemberDTO>> searchMembersByName(@RequestParam String name) {
        try {
            List<Member> members = memberService.searchMembersByName(name);
            return ResponseEntity.ok(members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Add member to a specific chapter
     */
    @PostMapping("/chapter/{chapterId}")
    public ResponseEntity<MemberDTO> addMemberToChapter(@PathVariable Long chapterId, @RequestBody Member member) {
        try {
            Member createdMember = memberService.addMemberToChapter(chapterId, member);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Transfer member to another chapter
     */
    @PutMapping("/{id}/transfer/{newChapterId}")
    public ResponseEntity<MemberDTO> transferMemberToChapter(@PathVariable Long id, @PathVariable Long newChapterId) {
        try {
            Member transferredMember = memberService.transferMemberToChapter(id, newChapterId);
            return ResponseEntity.ok(convertToDTO(transferredMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete member (hard delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
