package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    /**
     * Find institution by name (case-insensitive)
     */
    Optional<Institution> findByNameIgnoreCase(String name);

    /**
     * Find institutions by state
     */
    List<Institution> findByStateIgnoreCase(String state);

    /**
     * Find institutions by city
     */
    List<Institution> findByCityIgnoreCase(String city);

    /**
     * Find institutions by state and city
     */
    List<Institution> findByStateIgnoreCaseAndCityIgnoreCase(String state, String city);

    /**
     * Search institutions by name containing search term
     */
    List<Institution> findByNameContainingIgnoreCase(String searchTerm);

    /**
     * Find institutions by type (University or Church)
     */
    @Query("SELECT i FROM Institution i WHERE TYPE(i) = :institutionType")
    List<Institution> findByInstitutionType(@Param("institutionType") Class<? extends Institution> institutionType);

    /**
     * Check if institution exists by name
     */
    boolean existsByNameIgnoreCase(String name);
}