package com.example.railhub.mapper;

import com.example.railhub.dto.TrainDTO;
import com.example.railhub.entity.Train;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainMapper {

    @Mapping(source = "id", target = "trainId")
    @Mapping(source = "trainName", target = "trainName")
    Train toEntity(TrainDTO trainDTO);

    @Mapping(source = "trainId", target = "id")
    @Mapping(source = "trainName", target = "trainName")
    TrainDTO toDTO(Train train);
}
