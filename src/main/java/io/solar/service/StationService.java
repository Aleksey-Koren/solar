package io.solar.service;

import io.solar.entity.objects.Station;
import io.solar.repository.StationRepository;
import io.solar.utils.server.beans.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Service
public class StationService {

    private StationRepository stationRepository;

    @Autowired
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Optional<Station> findById(Long id) {
        return stationRepository.findById(id);
    }
}
