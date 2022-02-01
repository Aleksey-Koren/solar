package io.solar.mapper.exchange;

import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.entity.exchange.ExchangeOffer;
import io.solar.mapper.ProductMapper;
import io.solar.mapper.UserMapper;
import io.solar.mapper.object.BasicObjectViewMapper;
import io.solar.service.ProductService;
import io.solar.service.UserService;
import io.solar.service.exchange.ExchangeOfferService;
import io.solar.service.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExchangeOfferMapper {

    private final UserMapper userMapper;
    private final BasicObjectViewMapper basicObjectViewMapper;
    private final ProductMapper productMapper;
    private final ExchangeOfferService exchangeOfferService;
    private final ExchangeService exchangeService;
    private final ProductService productService;
    private final UserService userService;


    public ExchangeOfferDto toDto(ExchangeOffer entity) {
        return ExchangeOfferDto.builder()
                .id(entity.getId())
                .exchangeId(entity.getExchange().getId())
                .user(userMapper.toDtoWithIdAndTitle(entity.getUser()))
                .inventoryObject(entity.getInventoryObject() != null
                        ? basicObjectViewMapper.toDto(entity.getInventoryObject())
                        : null)
                .moneyAmount(entity.getMoneyAmount())
                .product(productMapper.toDto(entity.getProduct()))
                .productAmount(entity.getProductAmount())
                .offerType(entity.getOfferType())
                .build();
    }

    public ExchangeOffer toEntity(ExchangeOfferDto dto) {
        ExchangeOffer entity;

        if (dto.getId() != null) {
            entity = exchangeOfferService.getById(dto.getId());
        } else {
            entity = new ExchangeOffer();
        }

//        entity.setExchange(exchangeService.getById(dto.getExchange().getId()));
        entity.setUser(dto.getUser() != null ? userService.getById(dto.getUser().getId()) : null);
        entity.setMoneyAmount(dto.getMoneyAmount());
        entity.setProduct(dto.getProduct() != null ? productService.getById(dto.getProduct().getId()) : null);
        entity.setProductAmount(dto.getProductAmount());
        entity.setOfferType(dto.getOfferType());

        return entity;
    }
}