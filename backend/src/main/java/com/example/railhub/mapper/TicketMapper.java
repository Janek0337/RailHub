package com.example.railhub.mapper;

import com.example.railhub.dto.TicketDTO;
import com.example.railhub.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketDTO toDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setTicketId(ticket.getTicketId());
        ticketDTO.setPrice(ticket.getPrice());
        ticketDTO.setPurchaseTime(ticket.getPurchaseTime());

        if (ticket.getTicketType() != null) {
            ticketDTO.setTicketTypeName(ticket.getTicketType().getTicketName());
        }
        if (ticket.getStartStation() != null) {
            ticketDTO.setStartStationName(ticket.getStartStation().getStationName());
        }
        if (ticket.getDestinationStation() != null) {
            ticketDTO.setDestinationStationName(ticket.getDestinationStation().getStationName());
        }
        if (ticket.getTrain() != null) {
            ticketDTO.setTrainName(ticket.getTrain().getTrainName());
        }

        return ticketDTO;
    }
}
