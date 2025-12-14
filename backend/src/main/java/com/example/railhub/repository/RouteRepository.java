package com.example.railhub.repository;

import com.example.railhub.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    boolean existsByTrain_TrainId(Long trainId);
}
