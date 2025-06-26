package com.example.demo.dto;

import com.example.demo.enums.FeedbackType;
import lombok.Data;

@Data
public class FeedbackRequest {
    private Long userId;
    private FeedbackType feedback;
}