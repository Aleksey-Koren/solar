package io.solar.mapper;

import io.solar.dto.inventory.InventorySocketDto;
import io.solar.entity.inventory.InventorySocket;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.InventorySocketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
public class SocketMapper implements EntityDtoMapper<InventorySocket, InventorySocketDto> {

    private final InventorySocketRepository socketRepository;
    private final BasicObjectRepository basicObjectRepository;

    @Autowired
    public SocketMapper(InventorySocketRepository socketRepository, BasicObjectRepository basicObjectRepository) {
        this.socketRepository = socketRepository;
        this.basicObjectRepository = basicObjectRepository;
    }

    @Override
    public InventorySocket toEntity(InventorySocketDto dto) {

        return Objects.isNull(dto.getId())
                ? createSocket(dto)
                : findSocket(dto);
    }

    @Override
    public InventorySocketDto toDto(InventorySocket entity) {

        return new InventorySocketDto(entity.getId(),
                entity.getItem().getId(),
                entity.getItemTypeId(),
                entity.getAlias(),
                entity.getSortOrder());
    }

    private InventorySocket findSocket(InventorySocketDto dto) {
        InventorySocket socket = socketRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find inventory socket with id = %d", dto.getId())
                ));

        BasicObject basicObject = basicObjectRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find item with id = %d", dto.getItemId())));


        socket.setAlias(dto.getAlias());
        socket.setSortOrder(dto.getSortOrder());
        socket.setItem(basicObject);
        socket.setItemTypeId(dto.getItemTypeId());

        return socket;
    }

    private InventorySocket createSocket(InventorySocketDto dto) {

        BasicObject basicObject = basicObjectRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find item with id = %d", dto.getItemId())));

        return new InventorySocket(null, basicObject, dto.getItemTypeId(), dto.getSortOrder(), dto.getAlias());
    }
}
