package io.solar.repository;

import io.solar.entity.inventory.InventorySocket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventorySocketRepository extends JpaRepository<InventorySocket, Long> {

    List<InventorySocket> findAllByItemIdOrderBySortOrder(Long objectDescriptionId);

    void deleteAllByItemId(Long itemId);
}
