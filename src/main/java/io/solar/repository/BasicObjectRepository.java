package io.solar.repository;

import io.solar.entity.objects.BasicObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasicObjectRepository extends JpaRepository<BasicObject, Long> {

    void deleteAllByObjectTypeDescriptionId(Long hullId);

    List<Long> findAllByObjectTypeDescriptionId(Long hullId);

    @Query("select o from BasicObject o " +
            "join ObjectTypeDescription otd " +
            "where o.attachedToShip.id = ?1 and o.attachedToSocket is not null and otd.inventoryTypeId = ?2")
    List<BasicObject> getObjectsInSlotsByTypeId(Long objectId, Long TypeId);
}
