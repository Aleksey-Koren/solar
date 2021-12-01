package io.solar.facade;

import io.solar.mapper.StationMapper;
import io.solar.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationFacade {

    private final StationService stationService;
    private final StationMapper stationMapper;

    @Autowired
    public StationFacade(StationService stationService, StationMapper stationMapper) {
        this.stationService = stationService;
        this.stationMapper = stationMapper;
    }
}
