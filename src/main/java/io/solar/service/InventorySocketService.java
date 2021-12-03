package io.solar.service;

import io.solar.repository.InventorySocketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventorySocketService {

    private final InventorySocketRepository inventorySocketRepository;

    @Autowired
    public InventorySocketService(InventorySocketRepository inventorySocketRepository) {
        this.inventorySocketRepository = inventorySocketRepository;
    }

    public void deleteByItemId(Long itemId) {

        inventorySocketRepository.deleteByItemId(itemId);
    }
}
