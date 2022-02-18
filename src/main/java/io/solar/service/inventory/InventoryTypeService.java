package io.solar.service.inventory;

import io.solar.entity.inventory.InventoryType;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.InventoryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryTypeService {

    @Value("${app.object.types.generator}")
    private String generatorObjectTypeTitle;
    @Value("${app.object.types.large_generator}")
    private String largeGeneratorObjectTypeTitle;
    @Value("${app.object.types.battery}")
    private String batteryObjectTypeTitle;

    private final InventoryTypeRepository inventoryTypeRepository;

    public InventoryType save(InventoryType inventoryType) {
        return inventoryTypeRepository.save(inventoryType);
    }

    public Optional<InventoryType> findById(Long id) {
        return inventoryTypeRepository.findById(id);
    }

    public InventoryType getByTitle(String title) {

        return inventoryTypeRepository.findByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot found inventory type: " + title));
    }

    public Page<InventoryType> findAll(Pageable pageable) {
        return inventoryTypeRepository.findAll(pageable);
    }

    public List<InventoryType> findAllByTitleIn(List<String> typesTitle) {

        return inventoryTypeRepository.findAllByTitleIn(typesTitle);
    }

    public void delete(Long id) {
        inventoryTypeRepository.deleteById(id);
    }

    public boolean isGenerator(BasicObject item) {
        List<InventoryType> generatorTypes = findAllByTitleIn(
                List.of(generatorObjectTypeTitle, largeGeneratorObjectTypeTitle, batteryObjectTypeTitle)
        );

        return generatorTypes.contains(item.getObjectTypeDescription().getInventoryType());
    }
}
