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

        Route newRoute = new Route();
        newRoute.setTrain(trainRef);
        Route savedRoute = routeRepository.save(newRoute);
        Long newId = savedRoute.getRouteId();

        Set<Long> addedStationIds = new HashSet<>();
        for (RouteStationDTO stationDTO : payload.getStations()) {
            if (!addedStationIds.add(stationDTO.getStationId())) {
                throw new IllegalArgumentException("Nie można dodać tej samej stacji do trasy więcej niż raz.");
            }
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

        Train trainRef = trainRepository.getReferenceById(payload.getTrainId());
        route.setTrain(trainRef);

        Set<Long> payloadStationIds = new HashSet<>();
        for (RouteStationDTO stationDTO : payload.getStations()) {
            payloadStationIds.add(stationDTO.getStationId());
        }

        route.getStations().removeIf(rs -> !payloadStationIds.contains(rs.getStation().getStationId()));

        for (RouteStationDTO stationDTO : payload.getStations()) {
            Long stationId =  stationDTO.getStationId();

            Optional<Route_Station> existingStationOpt = route.getStations().stream()
                    .filter(rs -> rs.getStation().getStationId().equals(stationId))
                    .findFirst();

            if (existingStationOpt.isPresent()) {
                Route_Station existingStation = existingStationOpt.get();
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
