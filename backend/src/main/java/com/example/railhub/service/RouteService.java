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

import java.util.*;
import java.util.stream.Collectors;

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

        for (RouteStationDTO stationDTO : payload.getStations()) {
            Station stationRef = stationRepository.getReferenceById(stationDTO.getStationId());

            Route_Station newStation = routeStationMapper.toEntity(stationDTO);
            Route_Station_ID routeStationId = new Route_Station_ID();
            routeStationId.setRouteId(newId);
            routeStationId.setStationId(stationDTO.getStationId());

            newStation.setRoute(savedRoute);
            newStation.setStation(stationRef);
            newStation.setId(routeStationId);
            routeStationRepository.save(newStation);
        }
        return routeMapper.toDTO(newRoute);
    }

    @Transactional
    public RouteDTO updateRoute(Long routeId, RoutePayload payload) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        Map<Long, Route_Station> currentStationsMap = route.getStations().stream()
                .collect(Collectors.toMap(rs -> rs.getStation().getStationId(), rs -> rs));

        Set<Long> payloadStationIds = new HashSet<>();

        for (RouteStationDTO stationDTO : payload.getStations()) {
            Long stationId =  stationDTO.getStationId();
            payloadStationIds.add(stationId);

            if (currentStationsMap.containsKey(stationId)) {
                Route_Station existingStation = currentStationsMap.get(stationId);

                existingStation.setArrivalTime(stationDTO.getArrivalTime());
                existingStation.setDepartureTime(stationDTO.getDepartureTime());
                existingStation.setRouteKilometer(stationDTO.getRouteKilometer());
                existingStation.setStopOrder(stationDTO.getStopOrder());
            }
            else {
                Station stationRef = stationRepository.getReferenceById(stationId);
                Route_Station newStation = routeStationMapper.toEntity(stationDTO);

                newStation.setRoute(route);
                newStation.setStation(stationRef);
                newStation.setId(new Route_Station_ID(routeId, stationId));

                route.getStations().add(newStation);
            }
        }

        route.getStations().removeIf(rs -> !payloadStationIds.contains(rs.getId().getStationId()));
        Route savedRoute = routeRepository.save(route);
        return routeMapper.toDTO(savedRoute);
    }

    public List<List<RouteStationDTO>> getSpecificRoutes(RouteSearchDTO routeSearchDTO) {
        List<Long> matchingIds = routeStationRepository.findSpecificRoutes(
                routeSearchDTO.getStationFromId(), routeSearchDTO.getStationToId(), routeSearchDTO.getTime());

        List<List<RouteStationDTO>> result = new ArrayList<>();

        for (Long id : matchingIds) {
            List<Route_Station> routeStations = routeStationRepository.findStationsOnRoute(
                    id, routeSearchDTO.getStationFromId(), routeSearchDTO.getStationToId());

            List<RouteStationDTO> DTOs = new ArrayList<>();
            for (Route_Station routeStation : routeStations) {
                DTOs.add(routeStationMapper.toDTO(routeStation));
            }
            result.add(DTOs);
        }

        return result;
    }
}
