package io.solar.facade;

import io.solar.dto.BasicObjectViewDto;
import io.solar.dto.StationDto;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;
import io.solar.mapper.StationMapper;
import io.solar.mapper.objects.BasicObjectViewMapper;
import io.solar.service.StationService;
import io.solar.specification.StationSpecification;
import io.solar.specification.filter.StationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StationFacade {

    private final StationService stationService;
    private final StationMapper stationMapper;
    private final BasicObjectViewMapper basicObjectViewMapper;

    @Autowired
    public StationFacade(StationService stationService, StationMapper stationMapper, BasicObjectViewMapper basicObjectViewMapper) {
        this.stationService = stationService;
        this.stationMapper = stationMapper;
        this.basicObjectViewMapper = basicObjectViewMapper;
    }

    public Page<BasicObjectViewDto> findAllAsBasicObjects(Pageable pageable, StationFilter stationFilter) {
        Page<Station> stations = stationService.findAll(new StationSpecification(stationFilter), pageable);
        Page<BasicObject> stationsAsObjects = stations.map(BasicObject.class::cast);
        return stationsAsObjects.map(basicObjectViewMapper::toDto);
    }

    public Optional<StationDto> findById(Long id) {
        Optional<Station> station = stationService.findById(id);
        return station.isPresent() ? Optional.of(stationMapper.toDto(station.get())) : Optional.empty();
    }

    public StationDto save(StationDto dto) {
        return stationMapper.toDto(stationService.save(stationMapper.toEntity(dto)));
    }
}