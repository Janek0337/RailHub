package com.example.railhub.repository;

import com.example.railhub.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query(nativeQuery = true, value = """
        SELECT COUNT(tic.ticket_id)
        FROM tickets tic
        JOIN route_station ticket_start ON tic.start_station_id = ticket_start.station_id AND tic.route_id = ticket_start.route_id
        JOIN route_station ticket_end ON tic.destination_station_id = ticket_end.station_id AND tic.route_id = ticket_end.route_id
        JOIN route_station user_start ON user_start.station_id = :stationFromId AND user_start.route_id = :routeId
        JOIN route_station user_end ON user_end.station_id = :stationToId AND user_end.route_id = :routeId
        WHERE tic.route_id = :routeId
          AND ticket_start.stop_order < user_end.stop_order
          AND ticket_end.stop_order > user_start.stop_order
    """)
    Integer countSoldTickets(
            @Param("stationFromId") Long stationFromId,
            @Param("stationToId") Long stationToId,
            @Param("routeId") Long routeId
    );
}