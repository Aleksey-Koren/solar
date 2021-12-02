package io.solar.mapper;

import io.solar.dto.ProductionDto;
import io.solar.entity.Production;
import io.solar.repository.ProductRepository;
import io.solar.repository.ProductionRepository;
import io.solar.repository.StationRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
@NoArgsConstructor
public class ProductionMapper {

    private ProductionRepository productionRepository;
    private ProductRepository productRepository;
    private StationRepository stationRepository;

    @Autowired
    public ProductionMapper(ProductionRepository productionRepository,
                            ProductRepository productRepository,
                            StationRepository stationRepository) {
        this.productionRepository = productionRepository;
        this.productRepository = productRepository;
        this.stationRepository = stationRepository;
    }

    public Production toEntity(ProductionDto dto) {
        Production production;
        if(dto.getId() != null) {
            production = productionRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Production with such id in database"));
        }else {
            production = new Production();
        }
            production.setProduct(dto.getProduct() != null ?
                    productRepository.findById(dto.getProduct())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Product with such id in database"))
                    : null);

            production.setPower(dto.getPower());

            production.setStation(dto.getStation() != null ?
                    stationRepository.findById(dto.getStation())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Station with such id in database"))
                    : null);
        return production;
    }

    public ProductionDto toDto(Production production) {
        ProductionDto dto = new ProductionDto();

        dto.setId(production.getId());
        dto.setProduct(production.getProduct() != null ? production.getProduct().getId() : null);
        dto.setPower(production.getPower());
        dto.setStation(production.getStation() != null ? production.getStation().getId() : null);
        return dto;
    }
}