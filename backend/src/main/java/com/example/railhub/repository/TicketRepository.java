package com.example.railhub.repository;

import com.example.railhub.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query(nativeQuery = true, value = """
        SELECT 
            (t.capacity - COUNT(tic.ticket_id)) AS available_seats
        FROM 
            routes r
        JOIN 
            trains t ON r.train_id = t.train_id
            
        JOIN 
            route_stations user_start 
            ON user_start.route_id = r.route_id AND user_start.station_id = :stationFromId
        JOIN 
            route_stations user_end 
            ON user_end.route_id = r.route_id AND user_end.station_id = :stationToId

        LEFT JOIN 
            tickets tic ON tic.route_id = r.route_id
            
        LEFT JOIN 
            route_stations ticket_start 
            ON ticket_start.route_id = r.route_id AND ticket_start.station_id = tic.start_station_id
        LEFT JOIN 
            route_stations ticket_end 
            ON ticket_end.route_id = r.route_id AND ticket_end.station_id = tic.destination_station_id

        WHERE 
            r.route_id = :routeId
            AND (
                tic.ticket_id IS NULL
                OR 
                (
                    ticket_start.stop_order < user_end.stop_order 
                    AND 
                    ticket_end.stop_order > user_start.stop_order
                )
            )
        GROUP BY 
            t.capacity
    """)
    Integer getTicketAvailability(
            @Param("stationFromId") Long stationFromId,
            @Param("stationToId") Long stationToId,
            @Param("routeId") Long routeId
    );
}