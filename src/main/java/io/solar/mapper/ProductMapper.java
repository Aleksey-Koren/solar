package io.solar.mapper;

import io.solar.dto.ProductDto;
import io.solar.entity.Product;
import io.solar.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductMapper {

    private ProductService productService;

    @Autowired
    public ProductMapper(ProductService productService) {
        this.productService = productService;
    }

    public Product toEntity(ProductDto dto) {
        Product product;
        if (dto.getId() != null) {
            product = productService.findById(dto.getId())
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