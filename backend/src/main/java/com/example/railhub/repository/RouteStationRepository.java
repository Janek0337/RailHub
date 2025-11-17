package com.example.railhub.repository;

import com.example.railhub.entity.Route_Station;
import com.example.railhub.entity.Route_Station_ID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteStationRepository extends JpaRepository<Route_Station, Route_Station_ID> {
    Optional<Route_Station> findTopByIdRouteIdOrderByStopOrderAsc(Long routeId);
    Optional<Route_Station> findTopByIdRouteIdOrderByStopOrderDesc(Long routeId);
    List<Route_Station> findById_RouteId(Long routeId);
}
