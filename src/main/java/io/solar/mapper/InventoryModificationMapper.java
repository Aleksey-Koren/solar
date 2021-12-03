package io.solar.mapper;

import io.solar.dto.inventory.InventoryModificationDto;
import io.solar.entity.inventory.InventoryModification;
import io.solar.repository.ObjectModificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
public class InventoryModificationMapper implements EntityDtoMapper<InventoryModification, InventoryModificationDto> {

    private final ObjectModificationTypeRepository objectModificationTypeRepository;

    @Autowired
    public InventoryModificationMapper(ObjectModificationTypeRepository objectModificationTypeRepository) {
        this.objectModificationTypeRepository = objectModificationTypeRepository;
    }

    @Override
    public InventoryModification toEntity(InventoryModificationDto dto) {

        return Objects.isNull(dto.getId())
                ? createModification(dto)
                : findModification(dto);
    }

    @Override
    public InventoryModificationDto toDto(InventoryModification entity) {

        return new InventoryModificationDto(
                entity.getId(),
                entity.getTitle(),
                entity.getData(),
                entity.getDescription()
        );
    }

    private InventoryModification findModification(InventoryModificationDto dto) {
        InventoryModification inventoryModification = objectModificationTypeRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find object modification with id = %d", dto.getId())
                ));

        inventoryModification.setTitle(dto.getTitle());
        inventoryModification.setData(dto.getData());
        inventoryModification.setDescription(dto.getDescription());

        return inventoryModification;
    }

    private InventoryModification createModification(InventoryModificationDto dto) {

        return InventoryModification.builder()
                .id(null)
                .title(dto.getTitle())
                .data(dto.getData())
                .description(dto.getDescription())
                .build();
    }
}