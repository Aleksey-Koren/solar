package io.solar.facade;

import io.solar.dto.object.BasicObjectViewDto;
import io.solar.dto.object.StationDto;
import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;
import io.solar.mapper.StationMapper;
import io.solar.service.StationService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.engine.interfaces.inventory.InventoryEngine;
import io.solar.service.object.BasicObjectService;
import io.solar.specification.StationSpecification;
import io.solar.specification.filter.StationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StationFacade {

    private final StationService stationService;
    private final StationMapper stationMapper;
    private final UserService userService;
    private final BasicObjectService basicObjectService;
    private final SpaceTechEngine spaceTechEngine;
    private final InventoryEngine inventoryEngine;

    public Page<StationDto> findAll(Pageable pageable, StationFilter stationFilter) {
        return stationService.findAll(new StationSpecification(stationFilter), pageable)
                .map(stationMapper::toListDto);
    }

    public Optional<StationDto> findById(Long id) {
        Optional<Station> station = stationService.findById(id);
        return station.isPresent() ? Optional.of(stationMapper.toDto(station.get())) : Optional.empty();
    }

    public StationDto save(StationDto dto) {
        return stationMapper.toDto(stationService.save(stationMapper.toEntity(dto)));
    }

    public void moveFromOwnerToStation(Long stationId , List<BasicObjectViewDto> dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        Station station = stationService.getById(stationId);
        List<BasicObject> items = dto.stream()
                .map(s -> basicObjectService.getById(s.getId()))
                .toList();
        inventoryEngine.moveFromOwnerToStation(items, user, station);
    }

    public void moveFromStationToOwner(Long stationId , List<BasicObjectViewDto> dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        Station station = stationService.getById(stationId);
        List<BasicObject> items = dto.stream()
                .map(s -> basicObjectService.getById(s.getId()))
                .toList();
        inventoryEngine.moveFromStationToOwner(items, user, station);
    }
}