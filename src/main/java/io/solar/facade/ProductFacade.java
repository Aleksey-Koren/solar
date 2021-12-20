package io.solar.facade;

import io.solar.dto.ProductDto;
import io.solar.entity.Product;
import io.solar.mapper.ProductMapper;
import io.solar.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final ProductMapper productMapper;

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
