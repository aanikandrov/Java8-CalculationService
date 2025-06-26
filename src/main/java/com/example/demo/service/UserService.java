package com.example.demo.service;

import com.example.demo.dto.FeedbackRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void updatePersonalConst(FeedbackRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", request.getUserId());
                    return new EntityNotFoundException("User not found");
                });

        double currentConst = user.getPersonalConst() != null ? user.getPersonalConst() : 0.0;
        double delta = switch (request.getFeedback()) {
            case TOO_LITTLE -> 0.2;
            case LITTLE -> 0.1;
            case NORMAL -> 0.0;
            case A_LOT -> -0.1;
            case TOO_MUCH -> -0.2;
        };

        double newConst = currentConst + delta;
        newConst = Math.max(-2.0, Math.min(2.0, newConst));

        user.setPersonalConst(newConst);
        userRepository.save(user);

        log.info("Updated personalConst for user {}: {} -> {} (feedback: {})",
                request.getUserId(), currentConst, newConst, request.getFeedback());
    }
}