package com.example.railhub.controller;

import com.example.railhub.dto.TrainDTO;
import com.example.railhub.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/trains")
@RequiredArgsConstructor
public class TrainController {
    private final TrainService trainService;

    @GetMapping
    public ResponseEntity<List<TrainDTO>> getAllTrains() {
        List<TrainDTO> trains = trainService.findAllTrains();
        return ResponseEntity.ok(trains);
    }

    @PutMapping ("/{id}")
    public ResponseEntity<TrainDTO> updateTrain(@PathVariable Long id, @RequestBody TrainDTO trainDTO) {
        TrainDTO updatedTrainDTO = trainService.updateTrain(id, trainDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTrainDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<TrainDTO> createTrain(@RequestBody TrainDTO trainDTO) {
        TrainDTO updatedTrainDTO = trainService.createTrain(trainDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedTrainDTO);
    }
}
