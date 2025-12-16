package com.example.railhub.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class TicketDTO {
    private Long ticketId;
    private String ticketTypeName;
    private String startStationName;
    private String destinationStationName;
    private String trainName;
    private BigDecimal price;
    private LocalTime purchaseTime;
}
