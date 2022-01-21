package io.solar.dto.shop;

import io.solar.dto.GoodsDto;
import io.solar.dto.object.ObjectTypeDescriptionDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class StationShopDto {

    private Long id;
    private Long stationId;
    private String shopLevel;
    private List<ObjectTypeDescriptionDto> inventoryGoods;
    private List<GoodsDto> productGoods;
}
