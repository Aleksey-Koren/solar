package io.solar.repository;

import io.solar.entity.objects.BasicObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasicObjectRepository extends JpaRepository<BasicObject, Long>, JpaSpecificationExecutor<BasicObject> {

    void deleteAllByObjectTypeDescriptionId(Long hullId);

    List<Long> findAllByObjectTypeDescriptionId(Long hullId);

    List<BasicObject> findAllByAttachedToShipId(Long attachedToShipId);

    @Query("select o from BasicObject as o " +
            "join o.objectTypeDescription as otd " +
            "where o.attachedToShip.id = ?1 and o.attachedToSocket is not null and otd.inventoryTypeId = ?2")
    List<BasicObject> getObjectsInSlotsByTypeId(Long objectId, Integer TypeId);

    @Query("select o from BasicObject o " +
            "where o.status = 'IN_SPACE'" +
            "and" +
            "((o.x <= ?1 + ?3) and (o.x >= ?1 - ?3))" +
            "and" +
            "((o.y <= ?2 + ?3) and (o.y >= ?2 - ?3))")
    List<BasicObject> findAllInViewDistance(Float x, Float y, Float distance);

    @Query("select o from BasicObject o " +
            "where o.objectTypeDescription.title = 'Planet'" +
            "and" +
            "((o.x <= ?1 + ?3) and (o.x >= ?1 - ?3))" +
            "and" +
            "((o.y <= ?2 + ?3) and (o.y >= ?2 - ?3))" +
            "or o.x = ?1 and o.y = ?2")
    List<BasicObject> findAllWithZeroViewDistance(Float x, Float y, Float defaultDistance);
}