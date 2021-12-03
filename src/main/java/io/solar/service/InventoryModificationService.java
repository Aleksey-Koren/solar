package io.solar.service;

import io.solar.entity.inventory.InventoryModification;
import io.solar.repository.ObjectModificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryModificationService {

    private final ObjectModificationTypeRepository objectModificationTypeRepository;

    @Autowired
    public InventoryModificationService(ObjectModificationTypeRepository objectModificationTypeRepository) {
        this.objectModificationTypeRepository = objectModificationTypeRepository;
    }

    public List<InventoryModification> getAll() {
        return objectModificationTypeRepository.findAll();
    }

    public InventoryModification save(InventoryModification inventoryModification) {

        return objectModificationTypeRepository.save(inventoryModification);
    }

    public void delete(Long modificationId){
        objectModificationTypeRepository.deleteById(modificationId);
    }
}
