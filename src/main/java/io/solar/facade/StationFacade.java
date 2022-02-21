package io.solar.facade;

import io.solar.dto.object.BasicObjectViewDto;
import io.solar.dto.object.StationDto;
import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;
import io.solar.mapper.StationMapper;
import io.solar.service.StationService;
import io.solar.service.UserService;
import io.solar.service.object.BasicObjectService;
import io.solar.specification.StationSpecification;
import io.solar.specification.filter.StationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationFacade {

    private final StationService stationService;
    private final StationMapper stationMapper;
    private final UserService userService;
    private final BasicObjectService basicObjectService;

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

    public void putToInventory(Long stationId , BasicObjectViewDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        Station station = stationService.getById(stationId);
        BasicObject item = basicObjectService.getById(dto.getId());
        if(user.equals(station.getUser())) {

        }
    }
}