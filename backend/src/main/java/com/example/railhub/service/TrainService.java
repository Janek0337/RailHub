package com.example.railhub.service;

import com.example.railhub.dto.TrainDTO;
import com.example.railhub.entity.Station;
import com.example.railhub.entity.Train;
import com.example.railhub.exceptions.ResourceNotFoundException;
import com.example.railhub.mapper.TrainMapper;
import com.example.railhub.repository.TrainRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TrainService {
    private TrainRepository trainRepository;
    private TrainMapper trainMapper;

    public List<TrainDTO> findAllTrains() {
        List<Train> trains = trainRepository.findAll();
        return trains.stream()
                .map(t -> trainMapper.toDTO(t))
                .collect(Collectors.toList());
    }

    public void deleteTrain(Long id){
        trainRepository.deleteById(id);
    }

    public TrainDTO updateTrain(Long id, TrainDTO trainDTO) {
        Train trainToUpdate = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono pociągu o id: " + id));
        String newName = trainDTO.getTrainName().toUpperCase();
        List<Train> lookup = trainRepository.findByTrainName((newName));
        boolean nameTakenByAnother = lookup.stream()
                .anyMatch(train -> !train.getTrainName().equals(newName));
        if (nameTakenByAnother) {
            throw new IllegalArgumentException("Pociąg o nazwie: \'" + newName + "\' już istnieje");
        }
        trainToUpdate.setTrainName(newName);
        trainToUpdate.setCapacity(trainDTO.getCapacity());
        Train updated = trainRepository.save(trainToUpdate);
        return trainMapper.toDTO(updated);
    }

    public TrainDTO createTrain(TrainDTO trainDTO) {
        Train trainToCreate = trainMapper.toEntity(trainDTO);
        String newName = trainDTO.getTrainName().toUpperCase();
        List<Train> lookup = trainRepository.findByTrainName(newName);
        if (!lookup.isEmpty()) {
            throw new IllegalArgumentException("Pociąg o nazwie: \'" + newName + "\' już istnieje");
        }
        Train train = trainMapper.toEntity(trainDTO);
        train.setTrainName(newName);
        train.setCapacity(trainDTO.getCapacity());
        Train savedTrain = trainRepository.save(train);
        return trainMapper.toDTO(savedTrain);
    }

}
