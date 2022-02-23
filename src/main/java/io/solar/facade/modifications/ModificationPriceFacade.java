package io.solar.facade.modifications;

import io.solar.dto.modification.ModificationPriceDto;
import io.solar.entity.User;
import io.solar.entity.modification.ModificationPrice;
import io.solar.entity.objects.Station;
import io.solar.mapper.modification.ModificationPriceMapper;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.modification.ModificationPriceService;
import io.solar.specification.ModificationPriceSpecification;
import io.solar.specification.filter.ModificationPriceFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ModificationPriceFacade {

    private final ModificationPriceService modificationPriceService;
    private final ModificationPriceMapper modificationPriceMapper;
    private final SpaceTechEngine spaceTechEngine;
    private final StationService stationService;

    public List<ModificationPriceDto> findAll(ModificationPriceFilter modificationPriceFilter, Pageable pageable) {

        return modificationPriceService.findAll(new ModificationPriceSpecification(modificationPriceFilter), pageable)
                .map(modificationPriceMapper::toDto)
                .toList();
    }

    public HttpStatus createOrUpdateModificationPrice(ModificationPriceDto modificationPriceDto, User user) {
        Station station = stationService.getById(modificationPriceDto.getStationId());

        if (spaceTechEngine.isUserOwnsThisSpaceTech(user, station)) {
            modificationPriceService.save(modificationPriceMapper.toEntity(modificationPriceDto));
        } else {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.OK;
    }
}