package io.solar.facade;

import io.solar.dto.BasicObjectViewDto;
import io.solar.dto.StationDto;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;
import io.solar.mapper.StationMapper;
import io.solar.mapper.objects.BasicObjectMapper;
import io.solar.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StationFacade {

    private final StationService stationService;
    private final StationMapper stationMapper;
    private final BasicObjectMapper basicObjectMapper;

    @Autowired
    public StationFacade(StationService stationService, StationMapper stationMapper, BasicObjectMapper basicObjectMapper) {
        this.stationService = stationService;
        this.stationMapper = stationMapper;
        this.basicObjectMapper = basicObjectMapper;
    }

    public Page<BasicObjectViewDto> findAllAsBasicObjects(Pageable pageable) {
        Page<Station> stations = stationService.findAll(pageable);
        Page<BasicObject> stationsAsObjects = stations.map(BasicObject.class::cast);
        return stationsAsObjects.map(basicObjectMapper::toBasicObjectViewDto);
    }
}

