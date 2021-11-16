package io.solar.service;

import io.solar.entity.Planet;
import io.solar.repository.PlanetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanetService {

    private final PlanetRepository planetRepository;

    @Autowired
    public PlanetService(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    public Optional<Planet> findById (Long id) {
        return  planetRepository.findById(id);
    }

    public Page<Planet> findAll (Pageable pageable) {
      return planetRepository.findAll(pageable);
    }

    public List<Planet> findAllById(List<Long> ids) {
        return planetRepository.findAllById(ids);
    }

    public Planet save(Planet planet) {
        return planetRepository.save(planet);
    }

    public List<Planet> findAll() {
       return planetRepository.findAll();
    }
}
