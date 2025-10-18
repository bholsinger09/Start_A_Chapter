package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    // Find chapter by name (case-insensitive)
    Optional<Chapter> findByNameIgnoreCase(String name);

    // Find chapters by university name (case-insensitive)
    List<Chapter> findByUniversityNameContainingIgnoreCase(String universityName);

    // Find chapters by state (case-insensitive)
    List<Chapter> findByStateIgnoreCase(String state);

    // Find chapters by city (case-insensitive)
    List<Chapter> findByCityIgnoreCase(String city);

    // Find active chapters
    List<Chapter> findByActiveTrue();

    // Find inactive chapters
    List<Chapter> findByActiveFalse();

    // Find chapters by state and active status
    List<Chapter> findByStateIgnoreCaseAndActive(String state, Boolean active);

    // Custom query to find chapters by state with member count
    @Query("SELECT c FROM Chapter c WHERE c.state = :state AND c.active = true ORDER BY SIZE(c.members) DESC")
    List<Chapter> findActiveChaptersByStateOrderByMemberCount(@Param("state") String state);

    // Custom query to search chapters by multiple criteria
    @Query("SELECT c FROM Chapter c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:universityName IS NULL OR LOWER(c.universityName) LIKE LOWER(CONCAT('%', :universityName, '%'))) AND " +
            "(:state IS NULL OR LOWER(c.state) = LOWER(:state)) AND " +
            "(:city IS NULL OR LOWER(c.city) = LOWER(:city)) AND " +
            "(:active IS NULL OR c.active = :active)")
    List<Chapter> findChaptersByCriteria(
            @Param("name") String name,
            @Param("universityName") String universityName,
            @Param("state") String state,
            @Param("city") String city,
            @Param("active") Boolean active);

    // Check if a chapter with the same name and university exists
    boolean existsByNameIgnoreCaseAndUniversityNameIgnoreCase(String name, String universityName);

    // Count active chapters by state
    @Query("SELECT COUNT(c) FROM Chapter c WHERE c.state = :state AND c.active = true")
    Long countActiveChaptersByState(@Param("state") String state);

    // Find chapters with upcoming events
    @Query("SELECT DISTINCT c FROM Chapter c JOIN c.events e WHERE e.eventDateTime > CURRENT_TIMESTAMP AND e.active = true AND c.active = true")
    List<Chapter> findChaptersWithUpcomingEvents();

    // Find active chapters with members loaded
    @Query("SELECT DISTINCT c FROM Chapter c LEFT JOIN FETCH c.members WHERE c.active = true")
    List<Chapter> findByActiveTrueWithMembers();
}