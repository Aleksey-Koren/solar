package io.solar.service.inventory;

import io.solar.entity.inventory.InventoryType;
import io.solar.repository.InventoryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryTypeService {

    private final InventoryTypeRepository inventoryTypeRepository;

    public InventoryType save(InventoryType inventoryType) {
        return inventoryTypeRepository.save(inventoryType);
    }

    public Optional<InventoryType> findById(Long id) {
        return inventoryTypeRepository.findById(id);
    }

    public Page<InventoryType> findAll(Pageable pageable) {
        return inventoryTypeRepository.findAll(pageable);
    }

    public void delete(Long id) {
        inventoryTypeRepository.deleteById(id);
    }
}
