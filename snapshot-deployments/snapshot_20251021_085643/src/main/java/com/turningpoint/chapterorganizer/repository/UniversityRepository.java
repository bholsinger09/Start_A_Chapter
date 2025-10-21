package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {

    /**
     * Find university by name (case-insensitive)
     */
    Optional<University> findByNameIgnoreCase(String name);

    /**
     * Find universities by type
     */
    List<University> findByUniversityTypeIgnoreCase(String universityType);

    /**
     * Find universities by state
     */
    List<University> findByStateIgnoreCase(String state);

    /**
     * Find universities with student population greater than or equal to specified amount
     */
    List<University> findByStudentPopulationGreaterThanEqual(Integer minStudentPopulation);

    /**
     * Find universities by student population range
     */
    List<University> findByStudentPopulationBetween(Integer minStudentPopulation, Integer maxStudentPopulation);

    /**
     * Search universities by name containing search term
     */
    @Query("SELECT u FROM University u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<University> searchByName(@Param("searchTerm") String searchTerm);
}