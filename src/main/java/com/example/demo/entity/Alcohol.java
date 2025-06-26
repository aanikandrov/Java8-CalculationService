package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "alcohol")
public class Alcohol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alcohol_id")
    private Long alcoholId;

    private String name;

    private Double degree;

    @Column(name = "is_basic")
    private Boolean isBasic;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "savedDrinks")
    private Set<User> savedByUsers = new HashSet<>();
}