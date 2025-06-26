package com.example.demo.controller;

import com.example.demo.dto.FeedbackRequest;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/update-const")
    public ResponseEntity<Void> updatePersonalConst(
            @RequestBody FeedbackRequest request) {
        userService.updatePersonalConst(request);
        return ResponseEntity.ok().build();
    }
}