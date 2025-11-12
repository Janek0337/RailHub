package com.example.railhub.service;

import com.example.railhub.dto.TicketTypeDTO;
import com.example.railhub.entity.Ticket_Type;
import com.example.railhub.mapper.TicketTypeMapper;
import com.example.railhub.repository.TicketTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TicketTypeService {
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketTypeMapper ticketTypeMapper;

    public List<TicketTypeDTO> getAllTicketTypes() {
        List<Ticket_Type> ticketTypes = ticketTypeRepository.findAll();
        return ticketTypes.stream()
                .map(tt -> ticketTypeMapper.toDTO(tt))
                .collect(Collectors.toList());
    }

    public TicketTypeDTO createTicketType(TicketTypeDTO ticketTypeDTO) {
        Ticket_Type ticketType = ticketTypeMapper.toEntity(ticketTypeDTO);
        Ticket_Type tt = ticketTypeRepository.save(ticketType);
        return ticketTypeMapper.toDTO(tt);
    }

    public void deleteTicketType(Long id) {
        if (!ticketTypeRepository.existsById(id)) {
            System.out.println("Ticket Type not found");
        }
        ticketTypeRepository.deleteById(id);
    }
}
