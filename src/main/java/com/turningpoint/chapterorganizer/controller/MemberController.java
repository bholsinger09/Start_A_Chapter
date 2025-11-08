package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    // GET /api/members/{id} - Get member by ID
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Optional<Member> member = memberService.getMemberById(id);
        return member.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/members/username/{username} - Get member by email/username (for blog feature)
    @GetMapping("/username/{username}")
    public ResponseEntity<Member> getMemberByUsername(@PathVariable String username) {
        // Since we don't have a username field, we'll use email as username
        Optional<Member> member = memberService.getMemberByEmail(username);
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
                                             @Valid @RequestBody Member updateRequest) {
        try {
            Member updatedMember = memberService.updateMember(id, updateRequest);
            return ResponseEntity.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    // GET /api/members/chapter/{chapterId}/count - Count active members in chapter
    @GetMapping("/chapter/{chapterId}/count")
    public ResponseEntity<Long> countActiveMembers(@PathVariable Long chapterId) {
        Long count = memberService.countActiveMembersByChapter(chapterId);
        return ResponseEntity.ok(count);
    }
}
