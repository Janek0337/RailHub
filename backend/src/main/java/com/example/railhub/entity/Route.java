package com.example.railhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @NotNull
    @Column(nullable = false)
    private LocalTime time;

    @ManyToOne
    @JoinColumn(name="train_id", nullable = false)
    @NotNull
    private Train train;
}
