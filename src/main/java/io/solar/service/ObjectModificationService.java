package io.solar.service;

import io.solar.entity.objects.ObjectModification;
import io.solar.repository.ObjectModificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObjectModificationService {

    private final ObjectModificationRepository objectModificationRepository;

    @Autowired
    public ObjectModificationService(ObjectModificationRepository objectModificationRepository) {
        this.objectModificationRepository = objectModificationRepository;
    }

    public void deleteByItemId(Long itemId) {

        objectModificationRepository.deleteAllByItemId(itemId);
    }

    public List<ObjectModification> findAllObjectModifications(Long itemId) {

        return objectModificationRepository.findAllByItemId(itemId);
    }

    public void saveAll(List<ObjectModification> objectModifications) {

        objectModificationRepository.saveAll(objectModifications);
    }

    public void deleteModificationsWithItemId(List<Long> modificationsId, Long itemId) {

        objectModificationRepository.deleteAllByModificationIdInAndItemId(modificationsId, itemId);
    }

}
