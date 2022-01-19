package io.solar.service.shop;

import io.solar.entity.shop.StationShop;
import io.solar.repository.StationShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StationShopService {

    private final StationShopRepository stationShopRepository;

    public StationShop findShopByStationId(Long stationId) {

        return stationShopRepository.findByStationId(stationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot fount station shop"));
    }

}
