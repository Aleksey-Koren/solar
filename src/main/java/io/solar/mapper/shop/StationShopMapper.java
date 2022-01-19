package io.solar.mapper.shop;

import io.solar.dto.shop.StationShopDto;
import io.solar.entity.shop.StationShop;
import io.solar.mapper.GoodsMapper;
import io.solar.mapper.object.ObjectTypeDescriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StationShopMapper {

    private final ObjectTypeDescriptionMapper objectTypeDescriptionMapper;
    private final GoodsMapper goodsMapper;

    public StationShopDto toDto(StationShop entity) {

        return StationShopDto.builder()
                .id(entity.getId())
                .stationId(entity.getStation().getId())
                .shopLevel(entity.getShopLevel().name())
                .inventoryGoods(entity.getInventoryGoods().stream().map(objectTypeDescriptionMapper::toDto).toList())
                .productGoods(entity.getStation().getGoods().stream().map(goodsMapper::toDto).toList())
                .build();
    }
}
