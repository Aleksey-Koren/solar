package io.solar.service;


import io.solar.entity.Production;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.ProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductionService {

    private final ProductionRepository productionRepository;
    private final BasicObjectRepository basicObjectRepository;

    public Optional<Production> findById(Long id) {
        return productionRepository.findById(id);
    }

    @Transactional
    public void deleteAllByObjectDescriptionId(Long id) {
        List<Long> ids = basicObjectRepository.findAllByObjectTypeDescriptionId(id);

        productionRepository.deleteAllByStationIdIn(ids);
    }

}
