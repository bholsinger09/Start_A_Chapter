package com.turningpoint.chapterorganizer.exception;

/**
 * Exception thrown when chapter operations fail.
 * Named from the caller's perspective - chapter management operations.
 */
public class ChapterManagementException extends RuntimeException {
    
    private final ChapterOperationFailureReason reason;
    private final String userFriendlyMessage;
    
    public enum ChapterOperationFailureReason {
        CHAPTER_NOT_FOUND("Chapter not found"),
        DUPLICATE_CHAPTER("Chapter already exists at this university"),
        INVALID_CHAPTER_DATA("Chapter information is invalid"),
        CHAPTER_HAS_MEMBERS("Cannot delete chapter with active members"),
        UNIVERSITY_NOT_FOUND("University not found"),
        CHAPTER_INACTIVE("Chapter is not currently active");
        
        private final String defaultMessage;
        
        ChapterOperationFailureReason(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }
        
        public String getDefaultMessage() {
            return defaultMessage;
        }
    }
    
    public ChapterManagementException(ChapterOperationFailureReason reason) {
        super(reason.getDefaultMessage());
        this.reason = reason;
        this.userFriendlyMessage = reason.getDefaultMessage();
    }
    
    public ChapterManagementException(ChapterOperationFailureReason reason, String customMessage) {
        super(customMessage);
        this.reason = reason;
        this.userFriendlyMessage = customMessage;
    }
    
    public ChapterManagementException(ChapterOperationFailureReason reason, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.reason = reason;
        this.userFriendlyMessage = customMessage;
    }
    
    public ChapterOperationFailureReason getReason() {
        return reason;
    }
    
    public String getUserFriendlyMessage() {
        return userFriendlyMessage;
    }
    
    /**
     * Creates exception for chapter not found
     */
    public static ChapterManagementException chapterNotFound(Long chapterId) {
        return new ChapterManagementException(
            ChapterOperationFailureReason.CHAPTER_NOT_FOUND,
            "Chapter with ID " + chapterId + " not found"
        );
    }
    
    /**
     * Creates exception for duplicate chapter
     */
    public static ChapterManagementException duplicateChapter(String chapterName, String universityName) {
        return new ChapterManagementException(
            ChapterOperationFailureReason.DUPLICATE_CHAPTER,
            "Chapter '" + chapterName + "' already exists at " + universityName
        );
    }
    
    /**
     * Creates exception for inactive chapter
     */
    public static ChapterManagementException chapterInactive(String chapterName) {
        return new ChapterManagementException(
            ChapterOperationFailureReason.CHAPTER_INACTIVE,
            "Chapter '" + chapterName + "' is not currently active"
        );
    }
}