package io.solar.facade;

import io.solar.dto.BasicObjectViewDto;
import io.solar.entity.objects.StarShip;
import io.solar.mapper.object.BasicObjectViewMapper;
import io.solar.service.StarMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StarMapFacade {

    private final StarMapService starMapService;
    private final BasicObjectViewMapper basicObjectViewMapperMapper;


    public List<BasicObjectViewDto> getStarshipView(StarShip starShip) {
        return starMapService.findAllInViewDistance(starShip).stream()
                .map(basicObjectViewMapperMapper::toDto)
                .collect(Collectors.toList());
    }
}