package com.example.railhub.controller;

import com.example.railhub.dto.*;
import com.example.railhub.service.RouteService;
import com.example.railhub.service.StationService;
import com.example.railhub.service.TicketService;
import com.example.railhub.service.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/browse")
@RequiredArgsConstructor
public class PublicController {
    private final StationService stationService;
    private final RouteService routeService;
    private final TicketService ticketService;
    private final TicketTypeService ticketTypeService;

    @GetMapping("/stations")
    public ResponseEntity<List<StationDTO>> getAllStations(){
        List<StationDTO> stations = stationService.findAllStations();
        return ResponseEntity.ok(stations);
    }

    @PostMapping("/routes")
    public ResponseEntity<List<List<RouteStationDTO>>> getValidRoutes(@RequestBody RouteSearchDTO routeSearchDTO) {
        List<List<RouteStationDTO>> routes = routeService.getSpecificRoutes(routeSearchDTO);
        System.out.println(routes);
        return ResponseEntity.ok(routes);
    }

    @PostMapping("/tickets")
    public ResponseEntity<Void> bookTickets(@RequestBody BookTicketsRequestDTO bookTicketsRequestDTO) {
        ticketService.bookTickets(bookTicketsRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/availability")
    public ResponseEntity<TrainAvailabilityDTO> getTrainAvailability(@RequestParam(name = "from") Long stationFromId,
                                                                     @RequestParam(name = "to") Long stationToId,
                                                                     @RequestParam(name = "route") Long routeId) {
        return new ResponseEntity<>(ticketService.getTrainAvailability(stationFromId, stationToId, routeId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TicketTypeDTO>> getAllTicketTypes(){
        return new ResponseEntity<>(ticketTypeService.getAllTicketTypes(), HttpStatus.OK);
    }
}