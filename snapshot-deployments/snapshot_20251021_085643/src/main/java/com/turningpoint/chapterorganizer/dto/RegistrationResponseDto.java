package com.turningpoint.chapterorganizer.dto;

public class RegistrationResponseDto {
    private String message;
    private Long memberId;
    private String username;

    public RegistrationResponseDto() {}

    public RegistrationResponseDto(String message, Long memberId, String username) {
        this.message = message;
        this.memberId = memberId;
        this.username = username;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "RegistrationResponseDto{" +
                "message='" + message + '\'' +
                ", memberId=" + memberId +
                ", username='" + username + '\'' +
                '}';
    }
}