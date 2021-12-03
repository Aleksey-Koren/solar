package io.solar.mapper;

import io.solar.dto.inventory.InventoryItemDto;
import io.solar.dto.inventory.InventorySocketDto;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.repository.InventorySocketRepository;
import io.solar.repository.ObjectTypeDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryItemMapper implements EntityDtoMapper<ObjectTypeDescription, InventoryItemDto> {

    private final ObjectTypeDescriptionRepository objectTypeDescriptionRepository;
    private final InventorySocketRepository inventorySocketRepository;
    private final SocketMapper socketMapper;

    @Autowired
    public InventoryItemMapper(ObjectTypeDescriptionRepository objectTypeDescriptionRepository,
                               InventorySocketRepository inventorySocketRepository,
                               SocketMapper socketMapper) {

        this.objectTypeDescriptionRepository = objectTypeDescriptionRepository;
        this.inventorySocketRepository = inventorySocketRepository;
        this.socketMapper = socketMapper;
    }

    @Override
    public ObjectTypeDescription toEntity(InventoryItemDto dto) {
        ObjectTypeDescription objectTypeDescription = objectTypeDescriptionRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find object type description with id = %d", dto.getId())
                ));

        objectTypeDescription.setTitle(dto.getTitle());
        objectTypeDescription.setDescription(dto.getDescription());
        objectTypeDescription.setInventoryTypeId(dto.getInventoryType());
        objectTypeDescription.setCooldown(dto.getCooldown());
        objectTypeDescription.setMass(dto.getMass());
        objectTypeDescription.setDistance(dto.getDistance());
        objectTypeDescription.setDurability(dto.getDurability());
        objectTypeDescription.setEnergyConsumption(dto.getEnergyConsumption());
        objectTypeDescription.setPowerMin(dto.getPowerMin());
        objectTypeDescription.setPowerMax(dto.getPowerMax());
        objectTypeDescription.setPrice(dto.getPrice());
        objectTypeDescription.setPowerDegradation(dto.getPowerDegradation());

        return objectTypeDescription;
    }

    @Override
    public InventoryItemDto toDto(ObjectTypeDescription entity) {

//        todo: sockets and modifications?
//        List<InventorySocketDto> sockets = inventorySocketRepository.findAllByItemId(entity.getId())
//                .stream()
//                .map(socketMapper::toDto)
//                .collect(Collectors.toList());

        return InventoryItemDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .inventoryType(entity.getInventoryTypeId())
                .cooldown(entity.getCooldown())
                .mass(entity.getMass())
                .distance(entity.getDistance())
                .durability(entity.getDurability())
                .energyConsumption(entity.getEnergyConsumption())
                .powerMin(entity.getPowerMin())
                .powerMax(entity.getPowerMax())
                .price(entity.getPrice())
                .powerDegradation(entity.getPowerDegradation())
                .build();
    }
}
