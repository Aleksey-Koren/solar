package io.solar.service;

import io.solar.entity.objects.StarShip;
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
}