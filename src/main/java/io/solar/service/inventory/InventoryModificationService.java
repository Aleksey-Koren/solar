package io.solar.service.inventory;

import io.solar.entity.objects.ObjectModificationType;
import io.solar.repository.ObjectModificationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryModificationService {

    private final ObjectModificationTypeRepository objectModificationTypeRepository;

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
