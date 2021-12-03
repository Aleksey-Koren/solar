package io.solar.repository;

import io.solar.entity.inventory.InventoryModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectModificationTypeRepository extends JpaRepository<InventoryModification, Long> {
}
