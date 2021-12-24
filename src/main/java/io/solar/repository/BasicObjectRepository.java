package io.solar.repository;

import io.solar.entity.inventory.InventoryType;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasicObjectRepository extends JpaRepository<BasicObject, Long>, JpaSpecificationExecutor<BasicObject> {

    void deleteAllByObjectTypeDescriptionId(Long hullId);

    List<Long> findAllByObjectTypeDescriptionId(Long hullId);

    List<BasicObject> findAllByAttachedToShipId(Long attachedToShipId);

    @Query("select o from BasicObject as o " +
            "join o.objectTypeDescription as otd " +
            "where o.attachedToShip.id = :objectId and o.attachedToSocket is not null and otd.inventoryType = :type")
    List<BasicObject> getObjectsInSlotsByTypeId(@Param("objectId") Long objectId, @Param("type")InventoryType type);


    @Query("select o from BasicObject o " +
            "where " +
            "o.status = 'IN_SPACE'" +
            "and " +
            "(o.objectTypeDescription.subType <> 'STATIC')" +
            "and " +
            "(o.x between (:x - :distance) and (:x + :distance))" +
            "and " +
            "(o.y between (:y - :distance) and (:y + :distance))")
    List<BasicObject> findAllInViewDistance(@Param("x") Float x, @Param("y") Float y, @Param("distance") Float distance);


    @Query(value = "SELECT bs FROM BasicObject bs WHERE bs.status = 'IN_SPACE' " +
            "AND bs.objectTypeDescription.type IN :shipOrStationTypes " +
            "AND bs.objectTypeDescription.subType <> 'STATIC' " +
            "AND bs.positionIteration < :positionIteration")
    List<BasicObject> findObjectsToUpdateCoordinates(@Param("shipOrStationTypes") List<ObjectType> types,
                                                     @Param("positionIteration") Long positionIteration,
                                                     Pageable pageable);
}