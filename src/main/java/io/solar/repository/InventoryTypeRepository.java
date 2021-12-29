package io.solar.repository;

import io.solar.entity.inventory.InventoryType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryTypeRepository extends JpaRepository<InventoryType, Long> {

    @Cacheable("inventoryTypes")
    Optional<InventoryType> findByTitle(String type);
}