package com.example.railhub.mapper;

import com.example.railhub.dto.RouteStationDTO;
import com.example.railhub.entity.Route_Station;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RouteStationMapper {
    Route_Station toEntity(RouteStationDTO routeStationDTO);
    RouteStationDTO toDTO(Route_Station route);
}