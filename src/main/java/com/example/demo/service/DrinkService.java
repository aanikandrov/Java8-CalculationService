package com.example.demo.service;

import com.example.demo.entity.Alcohol;
import com.example.demo.repository.AlcoholRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrinkService {
    private final AlcoholRepository alcoholRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "allUserDrinks", key = "#userId")
    public List<Alcohol> getAllDrinksForUser(Long userId) {
        List<Alcohol> basicDrinks = alcoholRepository.findAll();

        List<Alcohol> userDrinks = alcoholRepository.findBySavedByUsersUserId(userId);

        log.info("Basic drinks: {}", basicDrinks.size());
        log.info("User drinks: {}", userDrinks.size());

        Set<Alcohol> allDrinks = new HashSet<>();
        allDrinks.addAll(basicDrinks);
        allDrinks.addAll(userDrinks);

        return allDrinks.stream()
                .sorted(Comparator.comparing(Alcohol::getName))
                .collect(Collectors.toList());
    }
}