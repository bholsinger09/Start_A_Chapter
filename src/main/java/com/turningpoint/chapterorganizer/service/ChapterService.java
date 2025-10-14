package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChapterService {

    private final ChapterRepository chapterRepository;

    @Autowired
    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    /**
     * Create a new chapter
     */
    public Chapter createChapter(Chapter chapter) {
        // Check if chapter already exists with the same name at the same university
        if (chapterRepository.existsByNameIgnoreCaseAndUniversityNameIgnoreCase(
                chapter.getName(), chapter.getUniversityName())) {
            throw new IllegalArgumentException("Chapter with this name already exists at the university");
        }

        // Ensure the chapter is active by default
        if (chapter.getActive() == null) {
            chapter.setActive(true);
        }

        return chapterRepository.save(chapter);
    }

    /**
     * Get chapter by ID
     */
    @Transactional(readOnly = true)
    public Optional<Chapter> getChapterById(Long id) {
        return chapterRepository.findById(id);
    }

    /**
     * Get chapter by name (case-insensitive)
     */
    @Transactional(readOnly = true)
    public Optional<Chapter> getChapterByName(String name) {
        return chapterRepository.findByNameIgnoreCase(name);
    }

    /**
     * Get all chapters
     */
    @Transactional(readOnly = true)
    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    /**
     * Get all active chapters
     */
    @Transactional(readOnly = true)
    public List<Chapter> getAllActiveChapters() {
        return chapterRepository.findByActiveTrue();
    }

    /**
     * Search chapters by state
     */
    @Transactional(readOnly = true)
    public List<Chapter> searchChaptersByState(String state) {
        return chapterRepository.findByStateIgnoreCaseAndActive(state, true);
    }

    /**
     * Search chapters by university name
     */
    @Transactional(readOnly = true)
    public List<Chapter> searchChaptersByUniversity(String universityName) {
        return chapterRepository.findByUniversityNameContainingIgnoreCase(universityName);
    }

    /**
     * Search chapters by multiple criteria
     */
    @Transactional(readOnly = true)
    public List<Chapter> searchChapters(String name, String universityName, String state,
            String city, Boolean active) {
        return chapterRepository.findChaptersByCriteria(name, universityName, state, city, active);
    }

    /**
     * Update an existing chapter
     */
    public Chapter updateChapter(Long id, Chapter updatedChapter) {
        Chapter existingChapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + id));

        // Check if the new name/university combination already exists for a different
        // chapter
        if (!existingChapter.getName().equalsIgnoreCase(updatedChapter.getName()) ||
                !existingChapter.getUniversityName().equalsIgnoreCase(updatedChapter.getUniversityName())) {

            if (chapterRepository.existsByNameIgnoreCaseAndUniversityNameIgnoreCase(
                    updatedChapter.getName(), updatedChapter.getUniversityName())) {
                throw new IllegalArgumentException("Chapter with this name already exists at the university");
            }
        }

        // Update the fields
        existingChapter.setName(updatedChapter.getName());
        existingChapter.setUniversityName(updatedChapter.getUniversityName());
        existingChapter.setState(updatedChapter.getState());
        existingChapter.setCity(updatedChapter.getCity());
        existingChapter.setDescription(updatedChapter.getDescription());

        if (updatedChapter.getActive() != null) {
            existingChapter.setActive(updatedChapter.getActive());
        }

        return chapterRepository.save(existingChapter);
    }

    /**
     * Soft delete a chapter (mark as inactive)
     */
    public void deleteChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + id));

        chapter.setActive(false);
        chapterRepository.save(chapter);
    }

    /**
     * Permanently delete a chapter (use with caution)
     */
    public void permanentlyDeleteChapter(Long id) {
        if (!chapterRepository.existsById(id)) {
            throw new IllegalArgumentException("Chapter not found with id: " + id);
        }
        chapterRepository.deleteById(id);
    }

    /**
     * Reactivate a chapter
     */
    public Chapter reactivateChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + id));

        chapter.setActive(true);
        return chapterRepository.save(chapter);
    }

    /**
     * Get chapters with upcoming events
     */
    @Transactional(readOnly = true)
    public List<Chapter> getChaptersWithUpcomingEvents() {
        return chapterRepository.findChaptersWithUpcomingEvents();
    }

    /**
     * Count active chapters by state
     */
    @Transactional(readOnly = true)
    public Long countActiveChaptersByState(String state) {
        return chapterRepository.countActiveChaptersByState(state);
    }

    /**
     * Get chapters by state ordered by member count
     */
    @Transactional(readOnly = true)
    public List<Chapter> getActiveChaptersByStateOrderByMemberCount(String state) {
        return chapterRepository.findActiveChaptersByStateOrderByMemberCount(state);
    }
}