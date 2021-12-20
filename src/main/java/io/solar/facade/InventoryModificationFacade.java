package io.solar.facade;

import io.solar.dto.ObjectModificationTypeDto;
import io.solar.entity.objects.ObjectModificationType;
import io.solar.mapper.ObjectModificationTypeMapper;
import io.solar.service.inventory.InventoryModificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InventoryModificationFacade {

    private final InventoryModificationService inventoryModificationService;
    private final ObjectModificationTypeMapper objectModificationTypeMapper;

    public List<ObjectModificationTypeDto> getAll() {

        return inventoryModificationService.getAll()
                .stream()
                .map(objectModificationTypeMapper::toDto)
                .collect(Collectors.toList());
    }

    public ObjectModificationTypeDto save(ObjectModificationTypeDto objectModificationTypeDto) {
        ObjectModificationType objectModificationType = objectModificationTypeMapper.toEntity(objectModificationTypeDto);

        return objectModificationTypeMapper.toDto(
                inventoryModificationService.save(objectModificationType)
        );
    }
}
