package com.example.railhub.controller;

import com.example.railhub.dto.TicketTypeDTO;
import com.example.railhub.entity.Ticket_Type;
import com.example.railhub.mapper.TicketTypeMapper;
import com.example.railhub.service.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/ticket-types")
@RequiredArgsConstructor
public class TicketTypeController {
    private final TicketTypeService ticketTypeService;

    @GetMapping
    public ResponseEntity<List<TicketTypeDTO>> getAllTicketTypes() {
        List<TicketTypeDTO> ticketTypes = ticketTypeService.getAllTicketTypes();
        return ResponseEntity.ok(ticketTypes);
    }

    @PostMapping
    public ResponseEntity<TicketTypeDTO> createTicketType(@RequestBody TicketTypeDTO ticketTypeDTO) {
        TicketTypeDTO savedTicketType = ticketTypeService.createTicketType(ticketTypeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTicketType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketType(@PathVariable Long id) {
        ticketTypeService.deleteTicketType(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketTypeDTO> editTicketType(
            @PathVariable Long id,
            @RequestBody TicketTypeDTO ticketTypeDTO) {

        TicketTypeDTO updatedTicketType = ticketTypeService.editTicketType(id, ticketTypeDTO);
        return ResponseEntity.ok(updatedTicketType);
    }
}
