package com.turningpoint.chapterorganizer.dto;

import com.turningpoint.chapterorganizer.entity.Chapter;
import java.util.List;
import java.util.Map;

public class SearchResultsDto {
    private List<Chapter> chapters;
    private long totalResults;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private Map<String, Object> facets;
    private List<String> suggestions;
    private long searchTimeMs;

    // Constructors
    public SearchResultsDto() {}

    public SearchResultsDto(List<Chapter> chapters, long totalResults, int totalPages, 
                           int currentPage, int pageSize, Map<String, Object> facets, 
                           List<String> suggestions, long searchTimeMs) {
        this.chapters = chapters;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.facets = facets;
        this.suggestions = suggestions;
        this.searchTimeMs = searchTimeMs;
    }

    // Getters and Setters
    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Map<String, Object> getFacets() {
        return facets;
    }

    public void setFacets(Map<String, Object> facets) {
        this.facets = facets;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public long getSearchTimeMs() {
        return searchTimeMs;
    }

    public void setSearchTimeMs(long searchTimeMs) {
        this.searchTimeMs = searchTimeMs;
    }
}