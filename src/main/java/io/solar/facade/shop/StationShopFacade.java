package io.solar.facade.shop;

import io.solar.dto.shop.StationShopDto;
import io.solar.entity.shop.StationShop;
import io.solar.mapper.shop.StationShopMapper;
import io.solar.service.shop.StationShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StationShopFacade {

    private final StationShopService stationShopService;
    private final StationShopMapper stationShopMapper;

    public StationShopDto getShopByStationId(Long stationId) {
        StationShop shop = stationShopService.findShopByStationId(stationId);

        return stationShopMapper.toDto(shop);
    }
}
