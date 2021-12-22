package io.solar.mapper;

import io.solar.dto.inventory.InventoryItemDto;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.repository.InventorySocketRepository;
import io.solar.repository.InventoryTypeRepository;
import io.solar.repository.ObjectTypeDescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ObjectTypeDescriptionMapper implements EntityDtoMapper<ObjectTypeDescription, InventoryItemDto> {

    private final ObjectTypeDescriptionRepository objectTypeDescriptionRepository;
    private final InventoryTypeRepository inventoryTypeRepository;

    @Override
    public ObjectTypeDescription toEntity(InventoryItemDto dto) {

        return Objects.isNull(dto.getId())
                ? createObjectTypeDescription(dto)
                : findObjectTypeDescription(dto);
    }

    @Override
    public InventoryItemDto toDto(ObjectTypeDescription entity) {

        return InventoryItemDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .inventoryType(entity.getInventoryType().getId())
                .cooldown(entity.getCooldown())
                .mass(entity.getMass())
                .distance(entity.getDistance())
                .durability(entity.getDurability())
                .energyConsumption(entity.getEnergyConsumption())
                .powerMin(entity.getPowerMin())
                .powerMax(entity.getPowerMax())
                .price(entity.getPrice())
                .type(entity.getType())
                .subType(entity.getSubType())
                .powerDegradation(entity.getPowerDegradation())
                .build();
    }

    private ObjectTypeDescription createObjectTypeDescription(InventoryItemDto dto) {
        ObjectTypeDescription objectTypeDescription = new ObjectTypeDescription();

        fillObjectTypeDescriptionFieldsByDto(objectTypeDescription, dto);

        return objectTypeDescription;
    }

    private ObjectTypeDescription findObjectTypeDescription(InventoryItemDto dto) {

        ObjectTypeDescription objectTypeDescription = objectTypeDescriptionRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find object type description with id = %d", dto.getId())
                ));

        fillObjectTypeDescriptionFieldsByDto(objectTypeDescription, dto);

        return objectTypeDescription;
    }

    private void fillObjectTypeDescriptionFieldsByDto(ObjectTypeDescription objectTypeDescription, InventoryItemDto dto) {

        objectTypeDescription.setTitle(dto.getTitle());
        objectTypeDescription.setDescription(dto.getDescription());
        objectTypeDescription.setInventoryType(inventoryTypeRepository.findById(dto.getInventoryType())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't find type with title [%d]", dto.getInventoryType()))));
        objectTypeDescription.setCooldown(dto.getCooldown());
        objectTypeDescription.setMass(dto.getMass());
        objectTypeDescription.setDistance(dto.getDistance());
        objectTypeDescription.setDurability(dto.getDurability());
        objectTypeDescription.setEnergyConsumption(dto.getEnergyConsumption());
        objectTypeDescription.setPowerMin(dto.getPowerMin());
        objectTypeDescription.setPowerMax(dto.getPowerMax());
        objectTypeDescription.setPrice(dto.getPrice());
        objectTypeDescription.setPowerDegradation(dto.getPowerDegradation());

    }
}
