package com.example.railhub.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class RouteSearchDTO {
    private Long stationFromId;
    private Long stationToId;
    private LocalTime time;
}
