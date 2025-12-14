package com.example.railhub.repository;

import com.example.railhub.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    List<Train> findByTrainName(String trainName);
}
