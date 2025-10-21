package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.EventRSVP;
import com.turningpoint.chapterorganizer.entity.RSVPStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRSVPRepository extends JpaRepository<EventRSVP, Long> {

    // Find RSVP by event and member
    Optional<EventRSVP> findByEventIdAndMemberId(Long eventId, Long memberId);

    // Find all RSVPs for a specific event
    List<EventRSVP> findByEventId(Long eventId);

    // Find all RSVPs by a specific member
    List<EventRSVP> findByMemberId(Long memberId);

    // Find RSVPs by event and status
    List<EventRSVP> findByEventIdAndStatus(Long eventId, RSVPStatus status);

    // Count RSVPs by event and status
    long countByEventIdAndStatus(Long eventId, RSVPStatus status);

    // Count total RSVPs for an event
    long countByEventId(Long eventId);

    // Find members attending a specific event
    @Query("SELECT r FROM EventRSVP r WHERE r.event.id = :eventId AND r.status = 'ATTENDING'")
    List<EventRSVP> findAttendingRSVPsByEvent(@Param("eventId") Long eventId);

    // Find events a member is attending
    @Query("SELECT r FROM EventRSVP r WHERE r.member.id = :memberId AND r.status = 'ATTENDING'")
    List<EventRSVP> findAttendingRSVPsByMember(@Param("memberId") Long memberId);

    // Check if member has RSVP'd to event
    boolean existsByEventIdAndMemberId(Long eventId, Long memberId);

    // Find RSVPs for events in a specific chapter
    @Query("SELECT r FROM EventRSVP r WHERE r.event.chapter.id = :chapterId")
    List<EventRSVP> findByChapterId(@Param("chapterId") Long chapterId);

    // Find pending RSVPs for upcoming events
    @Query("SELECT r FROM EventRSVP r WHERE r.status = 'PENDING' AND r.event.eventDateTime > CURRENT_TIMESTAMP")
    List<EventRSVP> findPendingRSVPsForUpcomingEvents();
    
    // Analytics methods for date-based queries
    @Query("SELECT COUNT(r) FROM EventRSVP r WHERE r.createdAt BETWEEN :start AND :end")
    Long countByRsvpDateBetween(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);
}