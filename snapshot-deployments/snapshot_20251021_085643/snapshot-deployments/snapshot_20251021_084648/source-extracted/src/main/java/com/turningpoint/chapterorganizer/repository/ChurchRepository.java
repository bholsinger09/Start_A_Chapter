package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Church;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChurchRepository extends JpaRepository<Church, Long> {

    /**
     * Find church by name (case-insensitive)
     */
    Optional<Church> findByNameIgnoreCase(String name);

    /**
     * Find churches by denomination
     */
    List<Church> findByDenominationIgnoreCase(String denomination);

    /**
     * Find churches by state
     */
    List<Church> findByStateIgnoreCase(String state);

    /**
     * Find churches by pastor name
     */
    List<Church> findByPastorNameContainingIgnoreCase(String pastorName);

    /**
     * Find churches with membership size greater than or equal to specified amount
     */
    List<Church> findByMembershipSizeGreaterThanEqual(Integer minMembershipSize);

    /**
     * Find churches by membership size range
     */
    List<Church> findByMembershipSizeBetween(Integer minMembershipSize, Integer maxMembershipSize);

    /**
     * Search churches by name containing search term
     */
    @Query("SELECT c FROM Church c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Church> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Find distinct denominations
     */
    @Query("SELECT DISTINCT c.denomination FROM Church c WHERE c.denomination IS NOT NULL ORDER BY c.denomination")
    List<String> findDistinctDenominations();
}