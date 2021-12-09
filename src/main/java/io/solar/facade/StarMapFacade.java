package io.solar.facade;

import io.solar.dto.BasicObjectViewDto;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.mapper.objects.BasicObjectMapper;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.StarMapService;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StarMapFacade {


    private final StarMapService starMapService;
    private final BasicObjectMapper basicObjectMapper;


    public List<BasicObjectViewDto> getStarshipView(StarShip starShip) {
        return starMapService.findAllInViewDistance(starShip);
    }
}
