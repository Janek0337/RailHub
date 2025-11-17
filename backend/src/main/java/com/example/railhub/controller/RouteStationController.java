package com.example.railhub.controller;

import com.example.railhub.dto.RouteStationDTO;
import com.example.railhub.service.RouteStationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("/admin/route-stations")
@AllArgsConstructor
public class RouteStationController {
    private RouteStationService routeStationService;

    @GetMapping("/{id}")
    public List<RouteStationDTO> findAllStationsOnRoute(@PathVariable Long routeId) {
        return routeStationService.findRouteStationsById(routeId);
    }
}
