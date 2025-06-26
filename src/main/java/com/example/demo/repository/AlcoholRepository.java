package com.example.demo.repository;

import com.example.demo.entity.Alcohol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlcoholRepository extends JpaRepository<Alcohol, Long> {
    List<Alcohol> findByIsBasicTrue();
    List<Alcohol> findBySavedByUsersUserId(Long userId);
}