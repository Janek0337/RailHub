package com.example.railhub.controller;

import com.example.railhub.dto.StationDTO;
import com.example.railhub.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/stations")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @GetMapping
    public ResponseEntity<List<StationDTO>> getAllStations(){
        List<StationDTO> stations = stationService.findAllStations();
        return ResponseEntity.ok(stations);
    }

    @PostMapping
    public ResponseEntity<StationDTO> createStation(@RequestBody StationDTO stationDTO){
        StationDTO createdStation =  stationService.createStation(stationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  deleteStation(@PathVariable Long id){
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationDTO> updateStation (
            @PathVariable Long id,
            @RequestBody StationDTO stationDTO
    ) {
        StationDTO updatedStation = stationService.updateStation(id, stationDTO);
        return ResponseEntity.ok(updatedStation);
    }
}
