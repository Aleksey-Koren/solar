package io.solar.mapper.price;

import io.solar.dto.price.PriceProductDto;
import io.solar.entity.price.PriceProduct;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.ProductService;
import io.solar.service.price.PriceProductService;
import io.solar.service.price.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceProductMapper implements EntityDtoMapper<PriceProduct, PriceProductDto> {

    private final ProductService productService;
    private final PriceService priceService;
    private final PriceProductService priceProductService;

    @Override
    public PriceProduct toEntity(PriceProductDto dto) {
        return dto.getId() == null
                ? createPriceProduct(dto)
                : updatePriceProduct(dto);
    }

    @Override
    public PriceProductDto toDto(PriceProduct entity) {

        return PriceProductDto.builder()
                .id(entity.getId())
                .priceId(entity.getPrice().getId())
                .amount(entity.getProductAmount())
                .productId(entity.getProduct().getId())
                .build();
    }

    private PriceProduct createPriceProduct(PriceProductDto dto) {

        return PriceProduct.builder()
                .product(productService.getById(dto.getProductId()))
                .productAmount(dto.getAmount())
                .price(priceService.getById(dto.getPriceId()))
                .build();
    }

    private PriceProduct updatePriceProduct(PriceProductDto dto) {
        PriceProduct priceProduct = priceProductService.getById(dto.getId());

        priceProduct.setProductAmount(dto.getAmount());
        priceProduct.setProduct(dto.getProductId() == null ? priceProduct.getProduct() : productService.getById(dto.getProductId()));
        priceProduct.setPrice(dto.getPriceId() == null ? priceProduct.getPrice() : priceService.getById(dto.getPriceId()));

        return priceProduct;
    }
}
