package io.solar.service;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import io.solar.entity.objects.StarShip;
import io.solar.repository.StarShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StarShipService {

    private final StarShipRepository starShipRepository;

    @Autowired
    public StarShipService(StarShipRepository starShipRepository) {
        this.starShipRepository = starShipRepository;
    }

    public Optional<StarShip> findById(Long id) {
        return starShipRepository.findById(id);
    }

}