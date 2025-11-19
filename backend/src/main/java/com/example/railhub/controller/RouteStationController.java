package com.example.railhub.controller;

import com.example.railhub.dto.RouteStationDTO;
import com.example.railhub.service.RouteStationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/route-stations")
public class RouteStationController {
    private RouteStationService routeStationService;

    @GetMapping("/{routeId}")
    public List<RouteStationDTO> findAllStationsOnRoute(@PathVariable Long routeId) {
        return routeStationService.findRouteStationsById(routeId);
    }
}
