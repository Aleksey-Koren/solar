package io.solar.mapper;

import io.solar.dto.GoodsDto;
import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.objects.BasicObject;
import io.solar.service.GoodsService;
import io.solar.service.ProductService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoodsMapper implements EntityDtoMapper<Goods, GoodsDto> {

    private final BasicObjectService basicObjectService;
    private final ProductService productService;
    private final GoodsService goodsService;

    @Override
    public Goods toEntity(GoodsDto dto) {

        return dto.getId() == null
                ? createGoods(dto)
                : updateGoods(dto);
    }

    @Override
    public GoodsDto toDto(Goods goods) {

        return GoodsDto.builder()
                .id(goods.getId())
                .station(goods.getOwner() != null ? goods.getOwner().getId() : null)
                .product(goods.getProduct() != null ? goods.getProduct().getId() : null)
                .amount(goods.getAmount())
                .buyPrice(goods.getBuyPrice())
                .sellPrice(goods.getSellPrice())
                .isAvailableForBuy(goods.getIsAvailableForBuy())
                .isAvailableForSale(goods.getIsAvailableForSale())
                .build();
    }

    private Goods createGoods(GoodsDto dto) {

        return Goods.builder()
                .owner(basicObjectService.getById(dto.getStation()))
                .product(productService.getById(dto.getProduct()))
                .amount(dto.getAmount())
                .buyPrice(dto.getBuyPrice())
                .sellPrice(dto.getSellPrice())
                .isAvailableForBuy(dto.getIsAvailableForBuy())
                .isAvailableForSale(dto.getIsAvailableForSale())
                .build();
    }

    private Goods updateGoods(GoodsDto dto) {
        Goods goods = goodsService.getById(dto.getId());

        goods.setAmount(dto.getAmount());
        goods.setBuyPrice(dto.getBuyPrice());
        goods.setSellPrice(dto.getSellPrice());
        goods.setIsAvailableForBuy(dto.getIsAvailableForBuy());
        goods.setIsAvailableForSale(dto.getIsAvailableForSale());

        return goods;
    }
}