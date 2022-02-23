package io.solar.mapper.price;

import io.solar.dto.price.PriceDto;
import io.solar.dto.price.PriceProductDto;
import io.solar.entity.price.Price;
import io.solar.entity.price.PriceProduct;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.price.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceMapper implements EntityDtoMapper<Price, PriceDto> {

    private final PriceService priceService;
    private final PriceProductMapper priceProductMapper;


    @Override
    public Price toEntity(PriceDto dto) {

        return dto.getId() == null
                ? createPrice(dto)
                : updatePrice(dto);
    }

    @Override
    public PriceDto toDto(Price entity) {
        List<PriceProductDto> priceProductDtoList = entity.getPriceProducts()
                .stream()
                .map(priceProductMapper::toDto)
                .toList();

        return PriceDto.builder()
                .id(entity.getId())
                .moneyAmount(entity.getMoneyAmount())
                .priceProductDtoList(priceProductDtoList)
                .build();
    }

    private Price createPrice(PriceDto dto) {

        return Price.builder()
                .moneyAmount(dto.getMoneyAmount())
                .priceProducts(retrievePriceProductList(dto))
                .build();
    }

    private Price updatePrice(PriceDto dto) {
        Price price = priceService.getById(dto.getId());
        List<PriceProduct> priceProducts = retrievePriceProductList(dto);

        price.setMoneyAmount(dto.getMoneyAmount() == null ? price.getMoneyAmount() : dto.getMoneyAmount());
        price.setPriceProducts(priceProducts.isEmpty() ? price.getPriceProducts() : priceProducts);

        return price;
    }

    private List<PriceProduct> retrievePriceProductList(PriceDto dto) {
        if (dto.getPriceProductDtoList() != null) {
            return dto.getPriceProductDtoList()
                    .stream()
                    .map(priceProductMapper::toEntity)
                    .toList();
        }

        return Collections.emptyList();
    }
}
