package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Event;
import com.turningpoint.chapterorganizer.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find events by chapter ID
    List<Event> findByChapterId(Long chapterId);

    // Find active events by chapter ID
    List<Event> findByChapterIdAndActiveTrue(Long chapterId);

    // Find events by chapter ID ordered by date
    List<Event> findByChapterIdOrderByEventDateTimeAsc(Long chapterId);

    // Find upcoming events by chapter ID
    @Query("SELECT e FROM Event e WHERE e.chapter.id = :chapterId AND e.eventDateTime > :now AND e.active = true ORDER BY e.eventDateTime ASC")
    List<Event> findUpcomingEventsByChapter(@Param("chapterId") Long chapterId, @Param("now") LocalDateTime now);

    // Find past events by chapter ID
    @Query("SELECT e FROM Event e WHERE e.chapter.id = :chapterId AND e.eventDateTime < :now AND e.active = true ORDER BY e.eventDateTime DESC")
    List<Event> findPastEventsByChapter(@Param("chapterId") Long chapterId, @Param("now") LocalDateTime now);

    // Find events by type
    List<Event> findByType(EventType type);

    // Find events by type and chapter ID
    List<Event> findByTypeAndChapterId(EventType type, Long chapterId);

    // Find events within a date range
    @Query("SELECT e FROM Event e WHERE e.eventDateTime BETWEEN :startDate AND :endDate ORDER BY e.eventDateTime ASC")
    List<Event> findEventsBetweenDates(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Find events within a date range for a specific chapter
    @Query("SELECT e FROM Event e WHERE e.chapter.id = :chapterId AND e.eventDateTime BETWEEN :startDate AND :endDate AND e.active = true ORDER BY e.eventDateTime ASC")
    List<Event> findEventsBetweenDatesByChapter(
            @Param("chapterId") Long chapterId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Find events by title (case-insensitive search)
    List<Event> findByTitleContainingIgnoreCase(String title);

    // Find events by location (case-insensitive search)
    List<Event> findByLocationContainingIgnoreCase(String location);

    // Custom query to search events by multiple criteria
    @Query("SELECT e FROM Event e WHERE " +
            "(:chapterId IS NULL OR e.chapter.id = :chapterId) AND " +
            "(:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:type IS NULL OR e.type = :type) AND " +
            "(:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:startDate IS NULL OR e.eventDateTime >= :startDate) AND " +
            "(:endDate IS NULL OR e.eventDateTime <= :endDate) AND " +
            "(:active IS NULL OR e.active = :active) " +
            "ORDER BY e.eventDateTime ASC")
    List<Event> findEventsByCriteria(
            @Param("chapterId") Long chapterId,
            @Param("title") String title,
            @Param("type") EventType type,
            @Param("location") String location,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("active") Boolean active);

    // Count upcoming events by chapter
    @Query("SELECT COUNT(e) FROM Event e WHERE e.chapter.id = :chapterId AND e.eventDateTime > :now AND e.active = true")
    Long countUpcomingEventsByChapter(@Param("chapterId") Long chapterId, @Param("now") LocalDateTime now);

    // Count events by type in a chapter
    @Query("SELECT COUNT(e) FROM Event e WHERE e.chapter.id = :chapterId AND e.type = :type AND e.active = true")
    Long countEventsByTypeInChapter(@Param("chapterId") Long chapterId, @Param("type") EventType type);

    // Find the next upcoming event for a chapter
    @Query("SELECT e FROM Event e WHERE e.chapter.id = :chapterId AND e.eventDateTime > :now AND e.active = true ORDER BY e.eventDateTime ASC")
    List<Event> findNextUpcomingEventByChapter(@Param("chapterId") Long chapterId, @Param("now") LocalDateTime now);

    // Find events that are nearly full (within 5 spots of capacity)
    @Query("SELECT e FROM Event e WHERE e.maxAttendees IS NOT NULL AND (e.maxAttendees - e.currentAttendees) <= 5 AND e.eventDateTime > :now AND e.active = true")
    List<Event> findEventsNearlyFull(@Param("now") LocalDateTime now);

    // Find events with available spots
    @Query("SELECT e FROM Event e WHERE (e.maxAttendees IS NULL OR e.currentAttendees < e.maxAttendees) AND e.eventDateTime > :now AND e.active = true ORDER BY e.eventDateTime ASC")
    List<Event> findEventsWithAvailableSpots(@Param("now") LocalDateTime now);
    
    // Analytics methods for date-based queries
    Long countByChapterId(Long chapterId);
    Long countByEventDateTimeBetween(LocalDateTime start, LocalDateTime end);
    Long countByEventDateTimeAfter(LocalDateTime after);
    List<Event> findByEventDateTimeAfter(LocalDateTime after);
    List<Event> findByEventDateTimeAfterOrderByEventDateTimeDesc(LocalDateTime after);
    
    // Additional methods for analytics service
    Long countByCreatedAtAfter(LocalDateTime after);
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    Long countByChapter(com.turningpoint.chapterorganizer.entity.Chapter chapter);
    Long countByChapterAndCreatedAtAfter(com.turningpoint.chapterorganizer.entity.Chapter chapter, LocalDateTime after);
    List<Event> findTop10ByOrderByCreatedAtDesc();
    List<Event> findByEventDateBetweenOrderByEventDateAsc(LocalDateTime start, LocalDateTime end);
    List<Event> findByEventDateTimeBetweenOrderByEventDateTimeAsc(LocalDateTime start, LocalDateTime end);
    Long countByChapterAndEventDateAfter(com.turningpoint.chapterorganizer.entity.Chapter chapter, LocalDateTime after);
    Long countByChapterAndEventDateTimeAfter(com.turningpoint.chapterorganizer.entity.Chapter chapter, LocalDateTime after);
}