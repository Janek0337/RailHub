package com.example.railhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @ManyToOne
    @JoinColumn(name="train_id", nullable = false)
    @NotNull
    private Train train;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Route_Station> stations = new ArrayList<>();


}
