package io.solar.service.object;

import io.solar.entity.objects.ObjectModification;
import io.solar.repository.ObjectModificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectModificationService {

    private final ObjectModificationRepository objectModificationRepository;

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
