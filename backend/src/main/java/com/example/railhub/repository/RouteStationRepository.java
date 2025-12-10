package com.example.railhub.repository;

import com.example.railhub.entity.Route_Station;
import com.example.railhub.entity.Route_Station_ID;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
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
    @Modifying
    void deleteAllByRouteId(Long routeId);

    @Query("SELECT rs.route.routeId, COUNT(rs.station) " +
            "FROM Route_Station rs " +
            "WHERE rs.station.stationId = :startId OR rs.station.stationId = :endId " +
            "GROUP BY rs.route.routeId " +
            "HAVING (SUM(CASE WHEN rs.station.stationId = :startId AND rs.departureTime > :time THEN 1 ELSE 0 END) > 0 " +
            "AND SUM(CASE WHEN rs.station.stationId = :endId THEN 1 ELSE 0 END) > 0)")
    List<Long> findSpecificRoutes(@Param("startId") Long startId, @Param("endId") Long endId, @Param("time") LocalTime time);

    @Query("SELECT rs FROM Route_Station rs " +
            "WHERE rs.route.routeId = :routeId AND " +
            "rs.stopOrder >= (SELECT rs.stopOrder FROM Route_Station rs WHERE rs.station.stationId = :startId AND rs.route.routeId = :routeId) AND " +
            "rs.stopOrder <= (SELECT rs.stopOrder FROM Route_Station rs WHERE rs.station.stationId = :endId AND rs.route.routeId = :routeId)")
    List<Route_Station> findStationsOnRoute(@Param("routeId") Long routeId, @Param("startId") Long startId, @Param("endId") Long endId);
}
