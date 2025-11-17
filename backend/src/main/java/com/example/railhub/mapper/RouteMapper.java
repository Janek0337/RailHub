package com.example.railhub.mapper;

import com.example.railhub.dto.RouteDTO;
import com.example.railhub.entity.Route;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    Route toEntity(RouteDTO routeDTO);
    RouteDTO toDTO(Route route);
}