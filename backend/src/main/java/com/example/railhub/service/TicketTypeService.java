package com.example.railhub.service;

import com.example.railhub.dto.TicketTypeDTO;
import com.example.railhub.entity.Ticket_Type;
import com.example.railhub.mapper.TicketTypeMapper;
import com.example.railhub.repository.TicketTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketTypeService {
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketTypeMapper ticketTypeMapper;

    public List<Ticket_Type> getAllTicketTypes() {
        return ticketTypeRepository.findAll();
    }

    public void saveTicketType(TicketTypeDTO ticketTypeDTO) {
        Ticket_Type ticketType = ticketTypeMapper.toEntity(ticketTypeDTO);
        ticketTypeRepository.save(ticketType);
    }
}
