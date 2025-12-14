package com.example.railhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketOrderItem {
    private Long ticketTypeId;
    private int quantity;
}
