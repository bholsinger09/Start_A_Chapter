package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // GET /api/members - Get all members across all chapters
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    // GET /api/members/paginated - Get paginated members with sorting
    @GetMapping("/paginated")
    public ResponseEntity<Page<Member>> getPaginatedMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Member> members = memberService.getPaginatedMembers(pageable);
        return ResponseEntity.ok(members);
    }

    // GET /api/members/chapter/{chapterId}/all - Get all members (including inactive)
    @GetMapping("/chapter/{chapterId}/all")
    public ResponseEntity<List<Member>> getAllMembers(@PathVariable Long chapterId) {
        List<Member> members = memberService.getAllMembersByChapter(chapterId);
        return ResponseEntity.ok(members);
    }

    // GET /api/members/{id} - Get member by ID
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Optional<Member> member = memberService.getMemberById(id);
        return member.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/members/chapter/{chapterId} - Get members by chapter
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<Member>> getMembersByChapter(@PathVariable Long chapterId) {
        List<Member> members = memberService.getMembersByChapter(chapterId);
        return ResponseEntity.ok(members);
    }

    // GET /api/members/chapter/{chapterId}/active - Get active members by chapter
    @GetMapping("/chapter/{chapterId}/active")
    public ResponseEntity<List<Member>> getActiveMembersByChapter(@PathVariable Long chapterId) {
        List<Member> members = memberService.getMembersByChapter(chapterId);
        return ResponseEntity.ok(members);
    }

    // GET /api/members/chapter/{chapterId}/role/{role} - Get members by chapter and role
    @GetMapping("/chapter/{chapterId}/role/{role}")
    public ResponseEntity<List<Member>> getMembersByRole(@PathVariable Long chapterId, @PathVariable MemberRole role) {
        List<Member> members = memberService.getMembersByRole(chapterId, role);
        return ResponseEntity.ok(members);
    }

    // GET /api/members/chapter/{chapterId}/president - Get chapter president
    @GetMapping("/chapter/{chapterId}/president")
    public ResponseEntity<Member> getChapterPresident(@PathVariable Long chapterId) {
        Optional<Member> president = memberService.getChapterPresident(chapterId);
        return president.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/members/chapter/{chapterId}/search - Search members by name within chapter
    @GetMapping("/chapter/{chapterId}/search")
    public ResponseEntity<List<Member>> searchMembers(
            @PathVariable Long chapterId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MemberRole role,
            @RequestParam(required = false) String university,
            @RequestParam(required = false) String graduationYear,
            @RequestParam(required = false) Boolean active) {
        List<Member> members = memberService.searchMembers(chapterId, firstName, lastName, email, role, university, graduationYear, active);
        return ResponseEntity.ok(members);
    }

    // POST /api/members - Create new member
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        try {
            Member createdMember = memberService.createMember(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/members/{id} - Update existing member
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, 
                                             @Valid @RequestBody Member memberDetails) {
        try {
            Member updatedMember = memberService.updateMember(id, memberDetails);
            return ResponseEntity.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/members/{id}/role - Update member role
    @PutMapping("/{id}/role")
    public ResponseEntity<Member> updateMemberRole(@PathVariable Long id, @RequestBody MemberRole newRole) {
        try {
            Member updatedMember = memberService.updateMemberRole(id, newRole);
            return ResponseEntity.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/members/{id} - Deactivate member (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateMember(@PathVariable Long id) {
        try {
            memberService.deactivateMember(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/members/{id}/activate - Reactivate member
    @PutMapping("/{id}/activate")
    public ResponseEntity<Member> reactivateMember(@PathVariable Long id) {
        try {
            Member member = memberService.reactivateMember(id);
            return ResponseEntity.ok(member);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/members/chapter/{chapterId}/count - Count active members in chapter
    @GetMapping("/chapter/{chapterId}/count")
    public ResponseEntity<Long> countActiveMembers(@PathVariable Long chapterId) {
        Long count = memberService.countActiveMembersByChapter(chapterId);
        return ResponseEntity.ok(count);
    }

    // GET /api/members/chapter/{chapterId}/officers - Get chapter officers
    @GetMapping("/chapter/{chapterId}/officers")
    public ResponseEntity<List<Member>> getChapterOfficers(@PathVariable Long chapterId) {
        List<Member> officers = memberService.getChapterOfficers(chapterId);
        return ResponseEntity.ok(officers);
    }
}