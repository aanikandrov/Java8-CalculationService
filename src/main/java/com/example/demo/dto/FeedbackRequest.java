package com.example.demo.dto;

import com.example.demo.enums.FeedbackType;

public class FeedbackRequest {
    private Long userId;
    private FeedbackType feedback;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public FeedbackType getFeedback() {
        return feedback;
    }

    public void setFeedback(FeedbackType feedback) {
        this.feedback = feedback;
    }
}
