package io.solar.service.object;


import io.solar.entity.objects.BasicObject;
import io.solar.repository.BasicObjectRepository;
import io.solar.specification.BasicObjectSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BasicObjectService {

    private final BasicObjectRepository basicObjectRepository;

    @Autowired
    public BasicObjectService(BasicObjectRepository basicObjectRepository) {
        this.basicObjectRepository = basicObjectRepository;
    }

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
