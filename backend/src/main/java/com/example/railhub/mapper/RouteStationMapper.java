package com.example.railhub.mapper;

import com.example.railhub.dto.RouteStationDTO;
import com.example.railhub.entity.Route_Station;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RouteStationMapper {
    @Mapping(source = "station.stationId", target = "stationId")
    @Mapping(source = "arrivalTime", target = "arrivalTime")
    @Mapping(source = "departureTime", target = "departureTime")
    @Mapping(source = "stopOrder", target = "stopOrder")
    @Mapping(source = "routeKilometer", target = "routeKilometer")
    @Mapping(source = "route.routeId", target = "routeId")
    @Mapping(source = "route.train.trainName", target = "trainName")

    RouteStationDTO toDTO(Route_Station routeStation);
    Route_Station toEntity(RouteStationDTO routeStationDTO);
}
