package com.example.railhub.dto;

import lombok.Data;

@Data
public class TicketTypeDTO {
    private String ticketName;
    private int discountPercent;
    private Long ticketTypeId;
}
