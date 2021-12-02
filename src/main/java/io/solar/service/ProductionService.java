package io.solar.service;


import io.solar.entity.Production;
import io.solar.repository.ProductionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductionService {

    private ProductionRepository productionRepository;

    @Autowired
    public ProductionService(ProductionRepository productionRepository) {
        this.productionRepository = productionRepository;
    }

    public Optional<Production> findById(Long id) {
        return productionRepository.findById(id);
    }

}
