package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    List<Institution> findByState(String state);
    List<Institution> findByType(String type);
    List<Institution> findByNameContainingIgnoreCase(String name);
}