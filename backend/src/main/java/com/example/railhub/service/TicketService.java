package com.example.railhub.service;

import com.example.railhub.dto.*;
import com.example.railhub.entity.Route_Station_ID;
import com.example.railhub.entity.Ticket;
import com.example.railhub.mapper.TicketMapper;
import com.example.railhub.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {
    private final TicketTypeRepository ticketTypeRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final TicketRepository  ticketRepository;
    private final RouteStationRepository routeStationRepository;
    private final RouteRepository routeRepository;
    private final TicketMapper ticketMapper;

    public TicketService(TicketTypeRepository ticketTypeRepository, TrainRepository trainRepository, StationRepository stationRepository, TicketRepository ticketRepository, RouteStationRepository routeStationRepository, RouteRepository routeRepository, TicketMapper ticketMapper) {
        this.ticketTypeRepository = ticketTypeRepository;
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.ticketRepository = ticketRepository;
        this.routeStationRepository = routeStationRepository;
        this.routeRepository = routeRepository;
        this.ticketMapper = ticketMapper;
    }

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

    public List<TicketDTO> bookTickets(BookTicketsRequestDTO bookTicketsRequestDTO) {
        var route = routeRepository.findById(bookTicketsRequestDTO.getRouteId()).orElseThrow(() -> new RuntimeException("Route not found"));
        var train = route.getTrain();
        int capacity = train.getCapacity();
        int ticketsSold = getSoldTickets(bookTicketsRequestDTO.getStationFromId(), bookTicketsRequestDTO.getStationToId(), bookTicketsRequestDTO.getRouteId());

        int totalTicketsToBuy = bookTicketsRequestDTO.getTickets().stream().mapToInt(TicketOrderItem::getQuantity).sum();

        if (ticketsSold + totalTicketsToBuy > capacity) {
            throw new RuntimeException("No available seats for this train on the selected route.");
        }

        List<Ticket> savedTickets = new ArrayList<>();

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
                savedTickets.add(ticketRepository.save(ticketToBook));
            }
        }
        return savedTickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
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
        return new TrainAvailabilityDTO(availabilityRequestDTO.getRouteId(), capacity, ticketsSold);
    }

    public List<TrainAvailabilityDTO> getTrainAvailability(List<AvailabilityRequestDTO> availabilityRequestDTOs) {
        return availabilityRequestDTOs.stream()
                .map(this::getTrainAvailability)
                .filter(dto -> dto.getTicketsSold() < dto.getCapacity())
                .collect(Collectors.toList());
    }
}
