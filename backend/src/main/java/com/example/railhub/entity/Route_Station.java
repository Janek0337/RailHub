package com.example.railhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "route_station")
public class Route_Station {

    @EmbeddedId
    private Route_Station_ID id;

    @MapsId("routeId")
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @MapsId("stationId")
    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @NotNull
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @NotNull
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @NotNull
    @Column(name = "stop_order", nullable = false)
    private int stopOrder;

    @NotNull
    @Column(name = "route_kilometer", nullable = false)
    private int routeKilometer;
}
