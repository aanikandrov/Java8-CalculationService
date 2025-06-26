package com.example.demo.service;

import com.example.demo.dto.FeedbackUpdateRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserWithPersonalConst(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });
    }

    @Transactional
    public void updatePersonalConst(FeedbackUpdateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", request.getUserId());
                    return new EntityNotFoundException("User not found");
                });

        double delta = calculateDelta(request.getCategory());
        double newConst = calculateNewPersonalConst(user.getPersonalConst(), delta);

        user.setPersonalConst(newConst);
        userRepository.save(user);

        log.info("Updated personalConst for user {}: {} -> {} (category: {})",
                request.getUserId(), user.getPersonalConst(), newConst, request.getCategory());
    }

    private double calculateDelta(String category) {
        return switch (category) {
            case "Not enough", "Too little" -> 0.1;
            case "A lot", "Too much" -> -0.1;
            case "Just right" -> 0.0;
            default -> throw new IllegalArgumentException("Invalid category: " + category);
        };
    }

    private double calculateNewPersonalConst(Double currentConst, double delta) {
        double baseValue = (currentConst != null) ? currentConst : 0.7;
        double newValue = baseValue + delta;

        // Ограничиваем значение в диапазоне [0.0, 2.0]
        return Math.max(0.0, Math.min(2.0, newValue));
    }
}