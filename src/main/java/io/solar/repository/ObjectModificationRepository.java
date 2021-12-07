package io.solar.repository;

import io.solar.entity.objects.ObjectModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectModificationRepository extends JpaRepository<ObjectModification, Long> {

    void deleteAllByItemId(Long itemId);

    List<ObjectModification> findAllByItemId(Long itemId);

    void deleteAllByModificationIdInAndItemId(List<Long> modificationsId, Long itemId);
}
