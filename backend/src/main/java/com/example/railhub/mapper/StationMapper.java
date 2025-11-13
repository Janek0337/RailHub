package com.example.railhub.mapper;

import com.example.railhub.dto.StationDTO;
import com.example.railhub.entity.Station;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StationMapper {
    Station toEntity(StationDTO stationDTO);
    StationDTO toDTO(Station station);
}
