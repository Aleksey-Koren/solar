package io.solar.service;

import io.solar.entity.objects.Station;
import io.solar.repository.StationRepository;
import io.solar.specification.StationSpecification;
import io.solar.specification.filter.StationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    private final StationRepository stationRepository;

    @Autowired
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Optional<Station> findById(Long id) {
        return stationRepository.findById(id);
    }

    public Page<Station> findAll(StationSpecification stationSpecification, Pageable pageable) {
        return stationRepository.findAll(stationSpecification, pageable);
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public Station save(Station station) {
        return stationRepository.save(station);
    }

    public void deleteById(Long id) {
        stationRepository.deleteById(id);
    }
}
