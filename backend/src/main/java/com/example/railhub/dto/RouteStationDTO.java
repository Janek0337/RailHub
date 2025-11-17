package com.example.railhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStationDTO {
    private Long routeId;
    private Long stationId;
    private String stationName;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private int stopOrder;
    private int routeKilometer;
}
