package io.solar.service.modification;

import io.solar.entity.modification.Modification;
import io.solar.entity.modification.ModificationPrice;
import io.solar.entity.objects.Station;
import io.solar.repository.modification.ModificationPriceRepository;
import io.solar.specification.ModificationPriceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModificationPriceService {

    private final ModificationPriceRepository modificationPriceRepository;

    public Page<ModificationPrice> findAll(ModificationPriceSpecification specification, Pageable pageable) {

        return modificationPriceRepository.findAll(specification, pageable);
    }

    public Optional<ModificationPrice> findByStationAndModification(Station station, Modification modification) {
        return modificationPriceRepository.findByStationAndModification(station, modification);
    }

    public ModificationPrice getById(Long modificationPriceId) {

        return modificationPriceRepository.findById(modificationPriceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find modification price with id = " + modificationPriceId));
    }

    public Optional<ModificationPrice> findById(Long modificationPriceId) {

        return modificationPriceRepository.findById(modificationPriceId);
    }

    public ModificationPrice save(ModificationPrice modificationPrice) {

        return modificationPriceRepository.save(modificationPrice);
    }

    public void delete(ModificationPrice modificationPrice) {

        modificationPriceRepository.delete(modificationPrice);
    }

}