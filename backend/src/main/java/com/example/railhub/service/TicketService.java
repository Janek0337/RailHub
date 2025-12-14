package com.example.railhub.service;

import com.example.railhub.dto.BookTicketDTO;
import com.example.railhub.dto.TrainAvailabilityDTO;
import com.example.railhub.entity.Route_Station_ID;
import com.example.railhub.entity.Ticket;
import com.example.railhub.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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
        var route = routeRepository.findById(bookTicketDTO.getRouteId()).orElseThrow(() -> new RuntimeException("Route not found"));
        var train = route.getTrain();
        int capacity = train.getCapacity();
        int ticketsSold = getSoldTickets(bookTicketDTO.getStationFromId(), bookTicketDTO.getStationToId(), bookTicketDTO.getRouteId());

        if (ticketsSold >= capacity) {
            throw new RuntimeException("No available seats for this train on the selected route.");
        }
        Ticket ticketToBook = new Ticket();
        ticketToBook.setTicketType(ticketTypeRepository.findById(bookTicketDTO.getTicketTypeId()).get());
        ticketToBook.setPurchaseTime(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
        ticketToBook.setStartStation(stationRepository.findById(bookTicketDTO.getStationFromId()).get());
        ticketToBook.setDestinationStation(stationRepository.findById(bookTicketDTO.getStationToId()).get());
        ticketToBook.setRoute(route);
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

    public Integer getSoldTickets(Long stationFromId, Long stationToId, Long routeId) {
        return Optional.ofNullable(ticketRepository.countSoldTickets(stationFromId, stationToId, routeId)).orElse(0);
    }

    public TrainAvailabilityDTO getTrainAvailability(Long stationFromId, Long stationToId, Long routeId) {
        var route = routeRepository.findById(routeId).orElseThrow(() -> new RuntimeException("Route not found"));
        var train = route.getTrain();
        int capacity = train.getCapacity();
        int ticketsSold = getSoldTickets(stationFromId, stationToId, routeId);
        return new TrainAvailabilityDTO(capacity, ticketsSold);
    }
}
