package com.example.railhub.repository;

import com.example.railhub.entity.Ticket_Type;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<Ticket_Type, Long> {
}
