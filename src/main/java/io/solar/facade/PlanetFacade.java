package io.solar.facade;

import io.solar.dto.PlanetDto;
import io.solar.entity.Planet;
import io.solar.mapper.PlanetMapper;
import io.solar.service.PlanetService;
import io.solar.specification.filter.PlanetFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanetFacade {

    private final PlanetService planetService;
    private final PlanetMapper planetMapper;

    public Page<PlanetDto> findAll(PlanetFilter planetFilter, Pageable pageable) {

        return planetService.findAllFiltered(planetFilter, pageable).map(planetMapper::toDto);
    }

    public PlanetDto save(PlanetDto planetDto) {
        Planet planet = planetMapper.toEntity(planetDto);

        return planetMapper.toDto(planetService.save(planet));
    }

    public PlanetDto findById(Long planetId) {

        return planetMapper.toDto(planetService.getById(planetId));
    }

}
