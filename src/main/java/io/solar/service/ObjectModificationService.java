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

        objectModificationRepository.deleteByItemId(itemId);
    }

    public List<ObjectModification> findAllModifications(Long itemId) {

        return objectModificationRepository.findAllByItemId(itemId);
    }

}
