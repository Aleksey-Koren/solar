package io.solar.service.inventory;

import io.solar.entity.inventory.InventorySocket;
import io.solar.repository.InventorySocketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventorySocketService {

    private final InventorySocketRepository inventorySocketRepository;

    public void deleteByItemId(Long itemId) {

        inventorySocketRepository.deleteAllByItemId(itemId);
    }

    public List<InventorySocket> findAllSockets(Long itemId) {

        return inventorySocketRepository.findAllByItemIdOrderBySortOrder(itemId);
    }

    public void saveAll(List<InventorySocket> sockets) {

        inventorySocketRepository.saveAll(sockets);
    }

    public void deleteAll(List<InventorySocket> sockets) {

        inventorySocketRepository.deleteAll(sockets);
    }
}
