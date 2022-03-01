package io.solar.service;

import io.solar.entity.objects.Station;
import io.solar.repository.StationRepository;
import io.solar.specification.StationSpecification;
import io.solar.specification.filter.StationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    public Optional<Station> findById(Long id) {
        return stationRepository.findById(id);
    }

    public Station getById(Long stationId) {

        return stationRepository.findById(stationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find station with id = " + stationId));
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
        station.setMoney(station.getMoney() + amount);
        save(station);
        return station.getMoney();
    }
}
