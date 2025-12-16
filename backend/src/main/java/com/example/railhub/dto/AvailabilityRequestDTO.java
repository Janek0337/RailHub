package com.example.railhub.dto;

import lombok.Data;

@Data
public class AvailabilityRequestDTO {
    private Long stationFromId;
    private Long stationToId;
    private Long routeId;
}
