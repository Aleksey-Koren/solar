package io.solar.service.inventory;

import io.solar.entity.inventory.InventorySocket;
import io.solar.repository.InventorySocketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventorySocketService {

    private final InventorySocketRepository inventorySocketRepository;

    public Optional<InventorySocket> findById(Long socketId) {

        return inventorySocketRepository.findById(socketId);
    }

    public InventorySocket getById(Long socketId) {

        return inventorySocketRepository.findById(socketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find inventory socket with id = " + socketId));
    }

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
