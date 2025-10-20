package com.example.railhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private Ticket_Type ticketType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "start_station_id", nullable = false)
    private Station startStation;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;
}
