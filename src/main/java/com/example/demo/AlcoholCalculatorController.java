package com.example.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alcohol")
public class AlcoholCalculatorController {

    private final AlcoholCalculationService calculationService;

    public AlcoholCalculatorController(AlcoholCalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping("/calculate")
    public AlcoholCalculationResponse calculate(
            @RequestBody AlcoholCalculationRequest request
    ) {
        return calculationService.calculate(request);
    }
}