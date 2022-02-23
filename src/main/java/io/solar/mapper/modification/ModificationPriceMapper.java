package io.solar.mapper.modification;

import io.solar.dto.modification.ModificationPriceDto;
import io.solar.entity.modification.ModificationPrice;
import io.solar.mapper.EntityDtoMapper;
import io.solar.mapper.price.PriceMapper;
import io.solar.service.StationService;
import io.solar.service.modification.ModificationPriceService;
import io.solar.service.modification.ModificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificationPriceMapper implements EntityDtoMapper<ModificationPrice, ModificationPriceDto> {

    private final ModificationService modificationService;
    private final StationService stationService;
    private final ModificationPriceService modificationPriceService;
    private final PriceMapper priceMapper;

    @Override
    public ModificationPrice toEntity(ModificationPriceDto dto) {

        return dto.getId() == null
                ? createModificationPrice(dto)
                : updateModificationPrice(dto);
    }

    @Override
    public ModificationPriceDto toDto(ModificationPrice entity) {

        return ModificationPriceDto.builder()
                .id(entity.getId())
                .modificationId(entity.getModification().getId())
                .priceDto(priceMapper.toDto(entity.getPrice()))
                .stationId(entity.getStation().getId())
                .build();
    }

    private ModificationPrice createModificationPrice(ModificationPriceDto dto) {

        return ModificationPrice.builder()
                .modification(modificationService.getById(dto.getModificationId()))
                .price(dto.getPriceDto() != null ? priceMapper.toEntity(dto.getPriceDto()) : null)
                .station(stationService.getById(dto.getStationId()))
                .build();
    }

    private ModificationPrice updateModificationPrice(ModificationPriceDto dto) {
        ModificationPrice modificationPrice = modificationPriceService.getById(dto.getId());

        modificationPrice.setModification(dto.getModificationId() != null
                ? modificationService.getById(dto.getModificationId())
                : modificationPrice.getModification()
        );

        modificationPrice.setStation(dto.getStationId() != null
                ? stationService.getById(dto.getStationId())
                : modificationPrice.getStation()
        );

        modificationPrice.setPrice(dto.getPriceDto() != null
                ? priceMapper.toEntity(dto.getPriceDto())
                : modificationPrice.getPrice());


        return modificationPrice;
    }
}
