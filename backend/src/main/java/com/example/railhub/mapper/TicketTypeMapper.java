package com.example.railhub.mapper;

import com.example.railhub.dto.TicketTypeDTO;
import com.example.railhub.entity.Ticket_Type;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketTypeMapper {
    Ticket_Type toEntity(TicketTypeDTO ticketTypeDTO);
    TicketTypeDTO toDTO(Ticket_Type ticketType);
}