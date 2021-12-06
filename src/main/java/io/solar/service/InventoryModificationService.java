package io.solar.service;

import io.solar.entity.objects.ObjectModificationType;
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

    public List<ObjectModificationType> getAll() {

        return objectModificationTypeRepository.findAll();
    }

    public ObjectModificationType save(ObjectModificationType objectModificationType) {

        return objectModificationTypeRepository.save(objectModificationType);
    }

    public void delete(Long modificationId){

        objectModificationTypeRepository.deleteById(modificationId);
    }
}
