package com.example.railhub.repository;

import com.example.railhub.entity.Route_Station;
import com.example.railhub.entity.Route_Station_ID;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RouteStationRepository extends JpaRepository<Route_Station, Route_Station_ID> {
    Optional<Route_Station> findTopByIdRouteIdOrderByStopOrderAsc(Long routeId);
    Optional<Route_Station> findTopByIdRouteIdOrderByStopOrderDesc(Long routeId);

    @Query("SELECT rs FROM Route_Station rs JOIN FETCH rs.station s JOIN FETCH rs.route r WHERE r.routeId = :routeId ORDER BY rs.stopOrder ASC")
    List<Route_Station> findAllByRouteIdOptimized(@Param("routeId") Long routeId);

    @Transactional
    @Query("DELETE FROM Route_Station rs WHERE rs.id.routeId = :routeId")
    void deleteAllByRouteId(Long routeId);

}
