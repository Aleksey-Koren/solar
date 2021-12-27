package io.solar.service;

import io.solar.entity.Planet;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.PlanetRepository;
import io.solar.specification.PlanetSpecification;
import io.solar.specification.filter.PlanetFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanetService {

    private final PlanetRepository planetRepository;

    public Planet findById(Long id) {
        return planetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no planet with id = %s", id)));
    }

    public Page<Planet> findAll(Pageable pageable) {
        return planetRepository.findAll(pageable);
    }

    public Page<Planet> findAllFiltered(PlanetFilter filter, Pageable pageable) {
        return planetRepository.findAll(new PlanetSpecification(filter), pageable);
    }

    public Planet save(Planet planet) {
        return planetRepository.save(planet);
    }

    public List<Planet> saveAll(List<Planet> planets) {

        return planetRepository.saveAllAndFlush(planets);
    }

    public List<Planet> findAll() {

        return planetRepository.findAll();
    }

    public Planet findSun() {

        return planetRepository.findByPlanetIsNull();
    }
}
