package com.example.railhub.repository;

import com.example.railhub.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByStationName(String stationName);
}
