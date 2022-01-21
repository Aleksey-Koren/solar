package io.solar.mapper;

import io.solar.dto.ProductDto;
import io.solar.entity.Product;
import io.solar.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductMapper {

    private final ProductRepository productRepository;

    public Product toEntity(ProductDto dto) {
        Product product;
        if (dto.getId() != null) {
            product = productRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no such product ID in database"));
        }else{
            product = new Product();
        }

        product.setTitle(dto.getTitle());
        product.setImage(dto.getImage());
        product.setBulk(dto.getBulk());
        product.setMass(dto.getMass());
        product.setPrice(dto.getPrice());

        return product;
    }

    public ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();

        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setImage(product.getImage());
        dto.setBulk(product.getBulk());
        dto.setMass(product.getMass());
        dto.setPrice(product.getPrice());

        return dto;
    }
}