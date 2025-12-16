package com.example.railhub.mapper;

import com.example.railhub.dto.RouteDTO;
import com.example.railhub.entity.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    Route toEntity(RouteDTO routeDTO);
    @Mapping(source = "train.trainId", target = "trainId")
    RouteDTO toDTO(Route route);
}