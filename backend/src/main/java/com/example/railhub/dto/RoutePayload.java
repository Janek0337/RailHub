package com.example.railhub.dto;

import lombok.Data;
import java.util.List;

@Data
public class RoutePayload {
    private Long trainId;
    private List<RouteStationDTO> stations;
}
