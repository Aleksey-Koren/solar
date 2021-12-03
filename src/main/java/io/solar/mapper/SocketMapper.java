package io.solar.mapper;

import io.solar.dto.inventory.InventorySocketDto;
import io.solar.entity.inventory.InventorySocket;
import io.solar.repository.InventorySocketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SocketMapper implements EntityDtoMapper<InventorySocket, InventorySocketDto> {

    private final InventorySocketRepository socketRepository;

    @Autowired
    public SocketMapper(InventorySocketRepository socketRepository) {
        this.socketRepository = socketRepository;
    }

    @Override
    public InventorySocket toEntity(InventorySocketDto dto) {
        InventorySocket socket = socketRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find inventory socket with id = %d", dto.getId())
                ));

        socket.setAlias(dto.getAlias());
        socket.setSortOrder(dto.getSortOrder());
        socket.setItemId(dto.getItemId());
        socket.setItemTypeId(dto.getItemTypeId());

        return socket;
    }

    @Override
    public InventorySocketDto toDto(InventorySocket entity) {

        return new InventorySocketDto(entity.getId(),
                entity.getItemId(),
                entity.getItemTypeId(),
                entity.getAlias(),
                entity.getSortOrder());
    }
}
