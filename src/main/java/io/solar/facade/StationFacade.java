package io.solar.facade;

import io.solar.dto.StationDto;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;
import io.solar.mapper.StationMapper;
import io.solar.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<StationDto> findAllAsBasicObjects(Pageable pageable) {
        Page<Station> stations = stationService.findAll(pageable);
        Page<BasicObject> stationsAsObjects = stations
        return
    }
}

