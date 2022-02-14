package io.solar.mapper.inventory.socket;

import io.solar.dto.inventory.socket.SpaceTechSocketDto;
import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.inventory.InventorySocketService;
import io.solar.service.inventory.socket.SpaceTechSocketService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpaceTechSocketMapper implements EntityDtoMapper<SpaceTechSocket, SpaceTechSocketDto> {

    private final BasicObjectService basicObjectService;
    private final InventorySocketService inventorySocketService;
    private final SpaceTechSocketService spaceTechSocketService;

    @Override
    public SpaceTechSocket toEntity(SpaceTechSocketDto dto) {
        return dto.getId() == null
                ? createSocket(dto)
                : updateSocket(dto);
    }

    @Override
    public SpaceTechSocketDto toDto(SpaceTechSocket entity) {

        return SpaceTechSocketDto.builder()
                .id(entity.getId())
                .spaceTechId(entity.getSpaceTech().getId())
                .inventorySocketId(entity.getInventorySocket().getId())
                .energyConsumptionPriority(entity.getEnergyConsumptionPriority())
                .build();
    }

    private SpaceTechSocket createSocket(SpaceTechSocketDto dto) {

        return SpaceTechSocket.builder()
                .spaceTech(basicObjectService.getById(dto.getSpaceTechId()))
                .inventorySocket(inventorySocketService.getById(dto.getInventorySocketId()))
                .energyConsumptionPriority(dto.getEnergyConsumptionPriority())
                .build();
    }

    private SpaceTechSocket updateSocket(SpaceTechSocketDto dto) {
        SpaceTechSocket spaceTechSocket = spaceTechSocketService.getById(dto.getId());

        spaceTechSocket.setSpaceTech(dto.getSpaceTechId() == null
                ? spaceTechSocket.getSpaceTech()
                : basicObjectService.getById(dto.getSpaceTechId())
        );

        spaceTechSocket.setInventorySocket(dto.getInventorySocketId() == null
                ? spaceTechSocket.getInventorySocket()
                : inventorySocketService.getById(dto.getInventorySocketId())
        );

        spaceTechSocket.setEnergyConsumptionPriority(dto.getEnergyConsumptionPriority());

        return spaceTechSocket;
    }
}
