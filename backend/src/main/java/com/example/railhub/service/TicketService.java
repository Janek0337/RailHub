package com.example.railhub.service;

import com.example.railhub.dto.AvailabilityRequestDTO;
import com.example.railhub.dto.BookTicketDTO;
import com.example.railhub.dto.BookTicketsRequestDTO;
import com.example.railhub.dto.TicketOrderItem;
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

    public void bookTicket(BookTicketDTO bookTicketDTO) {
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
        ticketToBook.setTrain(route.getTrain());
        ticketToBook.setPrice(BigDecimal.valueOf(this.countPrice(bookTicketDTO)));

        ticketRepository.save(ticketToBook);
    }

    public void bookTickets(BookTicketsRequestDTO bookTicketsRequestDTO) {
        var route = routeRepository.findById(bookTicketsRequestDTO.getRouteId()).orElseThrow(() -> new RuntimeException("Route not found"));
        var train = route.getTrain();
        int capacity = train.getCapacity();
        int ticketsSold = getSoldTickets(bookTicketsRequestDTO.getStationFromId(), bookTicketsRequestDTO.getStationToId(), bookTicketsRequestDTO.getRouteId());

        int totalTicketsToBuy = bookTicketsRequestDTO.getTickets().stream().mapToInt(TicketOrderItem::getQuantity).sum();

        if (ticketsSold + totalTicketsToBuy > capacity) {
            throw new RuntimeException("No available seats for this train on the selected route.");
        }

        for (TicketOrderItem item : bookTicketsRequestDTO.getTickets()) {
            for (int i = 0; i < item.getQuantity(); i++) {
                Ticket ticketToBook = new Ticket();
                ticketToBook.setTicketType(ticketTypeRepository.findById(item.getTicketTypeId()).get());
                ticketToBook.setPurchaseTime(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
                ticketToBook.setStartStation(stationRepository.findById(bookTicketsRequestDTO.getStationFromId()).get());
                ticketToBook.setDestinationStation(stationRepository.findById(bookTicketsRequestDTO.getStationToId()).get());
                ticketToBook.setRoute(route);
                ticketToBook.setTrain(route.getTrain());
                ticketToBook.setPrice(BigDecimal.valueOf(this.countPrice(new BookTicketDTO(
                        bookTicketsRequestDTO.getStationFromId(),
                        bookTicketsRequestDTO.getStationToId(),
                        train.getTrainId(),
                        bookTicketsRequestDTO.getRouteId(),
                        item.getTicketTypeId()
                ))));
                ticketRepository.save(ticketToBook);
            }
        }
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

    public TrainAvailabilityDTO getTrainAvailability(AvailabilityRequestDTO availabilityRequestDTO) {
        var route = routeRepository.findById(availabilityRequestDTO.getRouteId()).orElseThrow(() -> new RuntimeException("Route not found"));
        var train = route.getTrain();
        int capacity = train.getCapacity();
        int ticketsSold = getSoldTickets(availabilityRequestDTO.getStationFromId(), availabilityRequestDTO.getStationToId(), availabilityRequestDTO.getRouteId());
        return new TrainAvailabilityDTO(capacity, ticketsSold);
    }
}
