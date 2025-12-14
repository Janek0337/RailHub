package com.example.railhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookTicketsRequestDTO {
    private Long routeId;
    private Long stationFromId;
    private Long stationToId;
    private List<TicketOrderItem> tickets;
}
