package io.solar.service;

import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.repository.StarShipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StarShipService {

    private final StarShipRepository starShipRepository;


    public Optional<StarShip> findById(Long id) {
        return starShipRepository.findById(id);
    }

    public StarShip save(StarShip starShip) {
        return starShipRepository.save(starShip);
    }

    public StarShip dockShip(StarShip starShip, Station station) {
        starShip.setStatus(ObjectStatus.ATTACHED_TO);
        starShip.setAttachedToShip(starShip);

        return starShipRepository.save(starShip);
    }

    public StarShip undockShip(StarShip starShip) {
        starShip.setStatus(ObjectStatus.IN_SPACE);
        starShip.setAttachedToShip(null);

        return starShipRepository.save(starShip);
    }
}