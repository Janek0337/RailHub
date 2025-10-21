package com.example.railhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "trains")
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "train_id")
    private Long trainId;

    @NotBlank
    @Column(name = "train_name", nullable = false)
    private String trainName;

    @NotNull
    @Column(nullable = false)
    private int capacity;
}
