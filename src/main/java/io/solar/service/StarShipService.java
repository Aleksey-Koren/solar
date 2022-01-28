package io.solar.service;

import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.ObjectType;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.repository.StarShipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StarShipService {

    private final StarShipRepository starShipRepository;


    public Optional<StarShip> findById(Long id) {
        return starShipRepository.findById(id);
    }

    public StarShip getById(Long starshipId) {

        return starShipRepository.findById(starshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find starship with id = " + starshipId));
    }

    public List<StarShip> findAllUserStarshipsInHangar(User user, Station station) {

        return starShipRepository.findAllByUserAndAttachedToShipAndObjectTypeDescription_TypeAndIdNot(user,
                station,
                ObjectType.SHIP,
                user.getLocation().getId()
        );
    }

    public StarShip save(StarShip starShip) {
        return starShipRepository.save(starShip);
    }

    public StarShip dockShip(StarShip starShip, Station station) {
        starShip.setStatus(ObjectStatus.ATTACHED_TO);
        starShip.setAttachedToShip(station);

        return starShipRepository.save(starShip);
    }

    public StarShip undockShip(StarShip starShip) {
        starShip.setStatus(ObjectStatus.IN_SPACE);
        starShip.setAttachedToShip(null);

        return starShipRepository.save(starShip);
    }

    public void delete(StarShip starShip) {
        starShipRepository.delete(starShip);
    }

    public boolean existsById(Long id) {
        return starShipRepository.existsById(id);
    }
}