package io.solar.repository;

import io.solar.entity.inventory.InventoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryTypeRepository extends JpaRepository<InventoryType, Long> {

}
