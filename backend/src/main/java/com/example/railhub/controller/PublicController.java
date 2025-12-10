package com.example.railhub.controller;

import com.example.railhub.dto.RouteSearchDTO;
import com.example.railhub.dto.RouteStationDTO;
import com.example.railhub.dto.RouteViewDTO;
import com.example.railhub.dto.StationDTO;
import com.example.railhub.service.RouteService;
import com.example.railhub.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/browse")
@RequiredArgsConstructor
public class PublicController {
    private final StationService stationService;
    private final RouteService routeService;

    @GetMapping("/stations")
    public ResponseEntity<List<StationDTO>> getAllStations(){
        List<StationDTO> stations = stationService.findAllStations();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/route")
    public ResponseEntity<List<List<RouteStationDTO>>> getValidRoutes(RouteSearchDTO routeSearchDTO) {
        List<List<RouteStationDTO>> routes = routeService.getSpecificRoutes(routeSearchDTO);
        return ResponseEntity.ok(routes);
    }
}