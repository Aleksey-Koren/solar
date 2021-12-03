package io.solar.facade;

import io.solar.dto.ProductDto;
import io.solar.entity.Product;
import io.solar.mapper.ProductMapper;
import io.solar.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductFacade {

    private ProductService productService;
    private ProductMapper productMapper;

    @Autowired
    public ProductFacade(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    public ProductDto save(ProductDto dto) {
        return productMapper.toDto(productService.save(productMapper.toEntity(dto)));
    }

    public Page<ProductDto> findAll (Pageable pageable) {
        return productService.findAll(pageable).map(productMapper::toDto);
    }

    public Optional<ProductDto> findById(Long id) {
        Optional<Product> product = productService.findById(id);
        return product.isPresent() ? Optional.of(productMapper.toDto(product.get())) : Optional.of(null);
    }
}
