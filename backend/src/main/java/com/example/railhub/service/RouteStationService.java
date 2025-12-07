package com.example.railhub.service;

import com.example.railhub.dto.RouteStationDTO;
import com.example.railhub.entity.Route_Station;
import com.example.railhub.mapper.RouteStationMapper;
import com.example.railhub.repository.RouteStationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RouteStationService {
    private RouteStationRepository routeStationRepository;
    private RouteStationMapper routeStationMapper;

    public List<RouteStationDTO> findRouteStationsById(Long routeId) {
        List<Route_Station> routeStations = routeStationRepository.findAllByRouteIdOptimized(routeId);
        return routeStations.stream()
                .map(routeStationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteRoutesStations(Long routeId) {
        routeStationRepository.deleteAllByRouteId(routeId);
    }
}