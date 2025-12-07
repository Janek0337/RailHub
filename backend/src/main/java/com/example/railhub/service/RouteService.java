package com.example.railhub.service;

import com.example.railhub.dto.*;
import com.example.railhub.entity.*;
import com.example.railhub.exceptions.ResourceNotFoundException;
import com.example.railhub.mapper.RouteMapper;
import com.example.railhub.mapper.RouteStationMapper;
import com.example.railhub.repository.RouteRepository;
import com.example.railhub.repository.RouteStationRepository;
import com.example.railhub.repository.StationRepository;
import com.example.railhub.repository.TrainRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RouteService {
    private final RouteStationMapper routeStationMapper;
    private RouteRepository routeRepository;
    private RouteStationRepository routeStationRepository;
    private StationRepository stationRepository;
    private RouteStationService routeStationService;
    private TrainRepository trainRepository;
    private RouteMapper routeMapper;

    public List<RouteViewDTO> findAllRoutes() {
        List<Route> routes = routeRepository.findAll();
        List<RouteViewDTO> routeViews = new ArrayList<>();
        for(Route route : routes) {
            RouteViewDTO routeViewDTO = new RouteViewDTO();
            routeViewDTO.setRouteId(route.getRouteId());
            routeViewDTO.setTrainName(route.getTrain().getTrainName());

            Optional<Route_Station> startStationId = routeStationRepository.findTopByIdRouteIdOrderByStopOrderAsc(route.getRouteId());
            if (startStationId.isPresent()) {
                Optional<Station> startStation = stationRepository.findById(startStationId.get().getId().getStationId());
                if (startStation.isPresent()) {
                    routeViewDTO.setStartStationName(startStation.get().getStationName());
                }
                else {
                    throw new ResourceNotFoundException("Route not found");
                }
            }

            Optional<Route_Station> endStationId = routeStationRepository.findTopByIdRouteIdOrderByStopOrderDesc(route.getRouteId());
            if (endStationId.isPresent()) {
                Optional<Station> endStation = stationRepository.findById(endStationId.get().getId().getStationId());
                if (endStation.isPresent()) {
                    routeViewDTO.setEndStationName(endStation.get().getStationName());
                }
                else {
                    throw new ResourceNotFoundException("Route not found");
                }
            }

            routeViews.add(routeViewDTO);
        }
        return routeViews;
    }

    @Transactional
    public void deleteRoute(Long id) {
        routeStationService.deleteRoutesStations(id);
        routeRepository.deleteById(id);
    }

    @Transactional
    public RouteDTO createRoute(RoutePayload payload) {
        Train trainRef = trainRepository.getReferenceById(payload.getTrainId());

        Route newRoute = new Route(null, trainRef);
        Route savedRoute = routeRepository.save(newRoute);
        Long newId = savedRoute.getRouteId();

        for (RouteStationDTO station : payload.getStations()) {
            Route_Station newStation = routeStationMapper.toEntity(station);
            Route_Station_ID routeStationId = new Route_Station_ID();
            routeStationId.setRouteId(newId);
            routeStationId.setStationId(station.getStationId());

            newStation.setRoute(savedRoute);
            newStation.setId(routeStationId);
            routeStationRepository.save(newStation);
        }
        return routeMapper.toDTO(newRoute);
    }
}
