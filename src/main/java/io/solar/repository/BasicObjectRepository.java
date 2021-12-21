package io.solar.repository;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.ObjectSubType;
import io.solar.entity.objects.ObjectType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
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
            "((o.x <= :x + :distance) and (o.x >= :x - :distance))" +
            "and" +
            "((o.y <= :y + :distance) and (o.y >= :y - :distance))")
    List<BasicObject> findAllInViewDistance(@Param("x") Float x, @Param("y") Float y, @Param("distance") Float distance);

    @Query("select o from BasicObject o " +
            "where o.objectTypeDescription.title = 'Planet'" +
            "and" +
            "((o.x <= ?1 + ?3) and (o.x >= ?1 - ?3))" +
            "and" +
            "((o.y <= ?2 + ?3) and (o.y >= ?2 - ?3))" +
            "or o.x = ?1 and o.y = ?2 and o.status = 'IN_SPACE'")
    List<BasicObject> findAllWithZeroViewDistance(Float x, Float y, Float defaultDistance);

    @Query(value = "SELECT bs FROM BasicObject bs WHERE bs.status = 'IN_SPACE' " +
            "AND bs.objectTypeDescription.type IN :shipOrStationTypes " +
            "AND bs.objectTypeDescription.subType <> 'STATIC' " +
            "AND bs.positionIteration < :positionIteration")
    List<BasicObject> findObjectsToUpdateCoordinates(@Param("shipOrStationTypes") List<ObjectType> types,
                                                     @Param("positionIteration") Long positionIteration,
                                                     Pageable pageable);
}