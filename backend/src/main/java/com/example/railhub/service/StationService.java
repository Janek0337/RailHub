package com.example.railhub.service;

import com.example.railhub.dto.StationDTO;
import com.example.railhub.entity.Station;
import com.example.railhub.mapper.StationMapper;
import com.example.railhub.repository.StationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StationService {
    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    public List<StationDTO> findAllStations() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .map(s -> stationMapper.toDTO(s))
                .collect(Collectors.toList());
    }

    public StationDTO createStation(StationDTO stationDTO) {
        Station station = stationMapper.toEntity(stationDTO);
        Station savedStation =  stationRepository.save(station);
        return stationMapper.toDTO(savedStation);
    }

    public void deleteStation(Long id){
        stationRepository.deleteById(id);
    }

    public StationDTO updateStation(Long id, StationDTO stationDTO) {
        stationRepository.findById(id)
                .orElseThrow(() -> new ResolutionException("Nie znaleziono stacji o id: " + id));
        Station stationToUpdate = stationMapper.toEntity(stationDTO);
        Station updated = stationRepository.save(stationToUpdate);
        return stationMapper.toDTO(updated);
    }
}
