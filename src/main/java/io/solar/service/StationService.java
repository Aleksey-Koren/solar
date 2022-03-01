package io.solar.service;

import io.solar.entity.objects.Station;
import io.solar.multithreading.StationMonitor;
import io.solar.repository.StationRepository;
import io.solar.specification.StationSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StationService {

    private final StationRepository stationRepository;
    private final StationMonitor stationMonitor;

    public Optional<Station> findById(Long id) {
        return stationRepository.findById(id);
    }

    public Station getById(Long stationId) {

        return stationRepository.findById(stationId)
                .orElseThrow(() -> {
                    log.warn("Cannot find station with id = {}", stationId);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Cannot find station with id = %d", stationId));
                });
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

    public Long increaseBalance(Station station, Long amount) {
        synchronized (stationMonitor.getMonitor(station.getId())) {
            station.setMoney(station.getMoney() + amount);
            save(station);
        }
        return station.getMoney();
    }
}