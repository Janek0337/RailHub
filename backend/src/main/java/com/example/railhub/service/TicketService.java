package com.example.railhub.service;

import com.example.railhub.dto.BookTicketDTO;
import com.example.railhub.entity.Route_Station_ID;
import com.example.railhub.entity.Ticket;
import com.example.railhub.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketService {
    private final TicketTypeRepository ticketTypeRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final TicketRepository  ticketRepository;
    private final RouteStationRepository routeStationRepository;
    private final RouteRepository routeRepository;

    public void bookTickets(BookTicketDTO bookTicketDTO) {
        Ticket ticketToBook = new Ticket();
        ticketToBook.setTicketType(ticketTypeRepository.findById(bookTicketDTO.getTicketTypeId()).get());
        ticketToBook.setPurchaseTime(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
        ticketToBook.setStartStation(stationRepository.findById(bookTicketDTO.getStationFromId()).get());
        ticketToBook.setDestinationStation(stationRepository.findById(bookTicketDTO.getStationToId()).get());
        ticketToBook.setRoute(routeRepository.findById(bookTicketDTO.getRouteId()).get());
        ticketToBook.setPrice(BigDecimal.valueOf(this.countPrice(bookTicketDTO)));

        ticketRepository.save(ticketToBook);
    }

    private double countPrice(BookTicketDTO bookTicketDTO) {
        int znizka = ticketTypeRepository.findById(bookTicketDTO.getTicketTypeId()).get().getDiscountPercent();
        int kilometerFrom = routeStationRepository.findById(new Route_Station_ID(bookTicketDTO.getRouteId(), bookTicketDTO.getStationFromId())).get().getRouteKilometer();
        int kilometerTo = routeStationRepository.findById(new Route_Station_ID(bookTicketDTO.getRouteId(), bookTicketDTO.getStationToId())).get().getRouteKilometer();
        double costPerKilometer = 2;
        return (costPerKilometer * (kilometerTo - kilometerFrom))*(1-(double)(znizka/100));
    }

    public Integer getTrainFullness(Long stationFromId, Long stationToId, Long routeId) {
        return ticketRepository.getTicketAvailability(stationFromId, stationToId, routeId);
    }
}
