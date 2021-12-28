package io.solar.service.object;

import io.solar.entity.inventory.InventoryType;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.repository.BasicObjectRepository;
import io.solar.specification.BasicObjectSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicObjectService {

    private final BasicObjectRepository basicObjectRepository;

    public Optional<BasicObject> findById(Long id) {
        return basicObjectRepository.findById(id);
    }


    public Page<BasicObject> findAll(BasicObjectSpecification basicObjectSpecification, Pageable pageable) {

        return basicObjectRepository.findAll(basicObjectSpecification, pageable);
    }

    public List<BasicObject> findAllByAttachedShipId(Long attachedShipId) {

        return basicObjectRepository.findAllByAttachedToShipId(attachedShipId);
    }

    public BasicObject save(BasicObject basicObject) {

        return basicObjectRepository.save(basicObject);
    }

    public void deleteByHullId(Long hullId) {

        basicObjectRepository.deleteAllByObjectTypeDescriptionId(hullId);
    }
}