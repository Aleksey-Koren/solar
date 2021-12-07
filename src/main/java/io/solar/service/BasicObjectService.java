package io.solar.service;


import io.solar.entity.objects.BasicObject;
import io.solar.repository.BasicObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void deleteByHullId(Long hullId) {

        basicObjectRepository.deleteAllByObjectTypeDescriptionId(hullId);
    }
}
