package io.solar.facade;

import io.solar.dto.BasicObjectViewDto;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.repository.BasicObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StarMapFacade {

    private final BasicObjectRepository basicObjectRepository;

    public List<BasicObjectViewDto> getStarshipView(StarShip starShip) {
        List<BasicObject> radars = basicObjectRepository.getObjectsInSlotsByTypeId(starShip.getId(), 7L);

        //TODO I have to finish this method

        return null;
    }
}
