package io.solar.service.marketplace;

import io.solar.entity.marketplace.MarketplaceBet;
import io.solar.repository.marketplace.MarketplaceBetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketplaceBetService {

    private final MarketplaceBetRepository marketplaceBetRepository;

    public Optional<MarketplaceBet> findById(Long lotId) {

        return marketplaceBetRepository.findById(lotId);
    }

    public MarketplaceBet getById(Long lotId) {

        return marketplaceBetRepository.findById(lotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find lot bet with id = " + lotId));
    }

    public MarketplaceBet save(MarketplaceBet marketplaceBet) {

        return marketplaceBetRepository.save(marketplaceBet);
    }

}
