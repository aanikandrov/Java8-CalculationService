package com.example.demo.controller;

import com.example.demo.dto.AlcoholCalculationRequest;
import com.example.demo.dto.AlcoholCalculationResponse;
import com.example.demo.service.AlcoholCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/alcohol")
@RequiredArgsConstructor
public class AlcoholCalculatorController {
    private final AlcoholCalculationService calculationService;

    @PostMapping("/calculate")
    public ResponseEntity<AlcoholCalculationResponse> calculate(
            @RequestBody AlcoholCalculationRequest request) {
        log.info("Received calculation request: {}", request);

        AlcoholCalculationResponse response = calculationService.calculate(request);
        log.info("Returning response: {}", response);

        return ResponseEntity.ok(response);
    }
}