package io.solar.service.marketplace;

import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.repository.marketplace.MarketplaceLotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketplaceLotService {

    private final MarketplaceLotRepository marketplaceLotRepository;

    public Optional<MarketplaceLot> findById(Long lotId) {

        return marketplaceLotRepository.findById(lotId);
    }

    public MarketplaceLot getById(Long lotId) {

        return marketplaceLotRepository.findById(lotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find lot with id = " + lotId));
    }

    public MarketplaceLot save(MarketplaceLot lot) {
        return marketplaceLotRepository.save(lot);
    }

    public void delete(MarketplaceLot lot) {
        marketplaceLotRepository.delete(lot);
    }
}