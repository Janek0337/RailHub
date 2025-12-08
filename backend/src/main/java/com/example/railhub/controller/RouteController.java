package com.example.railhub.controller;

import com.example.railhub.dto.RouteDTO;
import com.example.railhub.dto.RoutePayload;
import com.example.railhub.dto.RouteStationDTO;
import com.example.railhub.dto.RouteViewDTO;
import com.example.railhub.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/routes")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<List<RouteViewDTO>> getAllRoutes() {
        List<RouteViewDTO> routes = routeService.findAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteDTO> updateRoute(@PathVariable Long id, @RequestBody RoutePayload payload) {
        RouteDTO updatedRouteDTO = routeService.updateRoute(id, payload);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRouteDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<RouteDTO> createRoute(@RequestBody RoutePayload payload) {
        RouteDTO createdRouteDTO = routeService.createRoute(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRouteDTO);
    }
}
