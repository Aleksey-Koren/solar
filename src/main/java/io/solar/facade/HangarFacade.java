package io.solar.facade;

import io.solar.dto.object.StarShipDto;
import io.solar.entity.User;
import io.solar.entity.objects.Station;
import io.solar.mapper.StarShipMapper;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HangarFacade {

    private final StarShipService starShipService;
    private final StarShipMapper starShipMapper;
    private final StationService stationService;

    public List<StarShipDto> getAllStarships(Long stationId, User user) {
        Station station = stationService.getById(stationId);

        return starShipService.findAllUserStarshipsInHangar(user, user.getLocation(), station)
                .stream()
                .map(starShipMapper::toDto)
                .toList();
    }

}
