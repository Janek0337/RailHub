package com.example.railhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainAvailabilityDTO {
    private Long routeId;
    private int capacity;
    private int ticketsSold;
}
