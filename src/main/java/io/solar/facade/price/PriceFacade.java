package io.solar.facade.price;

import io.solar.dto.price.PriceDto;
import io.solar.dto.price.PriceProductDto;
import io.solar.entity.User;
import io.solar.entity.price.Price;
import io.solar.entity.price.PriceProduct;
import io.solar.mapper.price.PriceMapper;
import io.solar.mapper.price.PriceProductMapper;
import io.solar.service.price.PriceProductService;
import io.solar.service.price.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceFacade {

    private final PriceService priceService;
    private final PriceProductService priceProductService;
    private final PriceMapper priceMapper;
    private final PriceProductMapper priceProductMapper;

    public PriceDto createPrice(PriceDto priceDto, User user) {
        Price price = priceMapper.toEntity(priceDto);
        price.getOwners().add(user);

        Price savedPrice = priceService.save(price);

        List<PriceProduct> priceProducts = priceDto.getPriceProductDtoList()
                .stream()
                .peek(priceProduct -> priceProduct.setPriceId(savedPrice.getId()))
                .map(priceProductMapper::toEntity)
                .toList();

        List<PriceProduct> savedPriceProducts = priceProductService.saveAll(priceProducts);
        savedPrice.setPriceProducts(savedPriceProducts);

        return priceMapper.toDto(savedPrice);
    }

    public PriceDto updatePrice(PriceDto priceDto, User user) {
        if (!isUserCanUpdatePrice(priceDto.getId(), user)) {
            log.warn("User don't have permission to update price with id = {}", priceDto.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Price price = priceMapper.toEntity(priceDto);
        List<Long> priceProductIds = priceDto.getPriceProductDtoList()
                .stream()
                .map(PriceProductDto::getId)
                .toList();

        priceProductService.deleteAllByPriceIdExceptExisting(priceDto.getId(), priceProductIds);

        return priceMapper.toDto(priceService.save(price));
    }

    private boolean isUserCanUpdatePrice(Long priceId, User user) {

        return priceService.getById(priceId).getOwners().contains(user);
    }
}