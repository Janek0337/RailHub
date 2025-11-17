package com.example.railhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteViewDTO {
    private Long routeId;
    private String startStationName;
    private String endStationName;
    private String trainName;
}
