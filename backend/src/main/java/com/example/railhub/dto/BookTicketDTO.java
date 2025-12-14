package com.example.railhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookTicketDTO {
    private Long stationFromId;
    private Long stationToId;
    private Long trainId;
    private Long routeId;
    private Long ticketTypeId;
}
