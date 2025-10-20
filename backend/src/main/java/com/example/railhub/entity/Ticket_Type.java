package com.example.railhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Ticket_Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_type_id")
    private Long ticketTypeId;

    @Column(name = "ticket_name", nullable = false)
    @NotBlank
    private String ticketName;

    @Column(name = "discount_percent", nullable = false)
    @NotNull
    private int discountPercent;
}
