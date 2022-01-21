package io.solar.service.marketplace;

import io.solar.entity.marketplace.LotBet;
import io.solar.repository.marketplace.LotBetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LotBetService {

    private final LotBetRepository lotBetRepository;

    public Optional<LotBet> findById(Long lotId) {

        return lotBetRepository.findById(lotId);
    }

    public LotBet getById(Long lotId) {

        return lotBetRepository.findById(lotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find lot bet with id = " + lotId));
    }

    public LotBet save(LotBet lotBet) {

        return lotBetRepository.save(lotBet);
    }

}
