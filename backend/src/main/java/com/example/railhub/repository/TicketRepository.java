package com.example.railhub.repository;

import com.example.railhub.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query(nativeQuery = true, value = """
        SELECT COUNT(t.ticket_id)
        FROM tickets t
        WHERE t.route_id = :routeId
        AND (SELECT rs.stop_order FROM route_station rs WHERE rs.route_id = t.route_id AND rs.station_id = t.start_station_id) < (SELECT rs.stop_order FROM route_station rs WHERE rs.route_id = :routeId AND rs.station_id = :stationToId)
        AND (SELECT rs.stop_order FROM route_station rs WHERE rs.route_id = t.route_id AND rs.station_id = t.destination_station_id) > (SELECT rs.stop_order FROM route_station rs WHERE rs.route_id = :routeId AND rs.station_id = :stationFromId)
    """)
    Integer countSoldTickets(
            @Param("stationFromId") Long stationFromId,
            @Param("stationToId") Long stationToId,
            @Param("routeId") Long routeId
    );
}