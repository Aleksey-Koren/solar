package io.solar.service.marketplace;

import io.solar.entity.marketplace.MarketplaceBet;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.repository.marketplace.MarketplaceLotRepository;
import io.solar.specification.MarketplaceLotSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketplaceLotService {

    private final MarketplaceLotRepository marketplaceLotRepository;

    public Page<MarketplaceLot> findAll(Pageable pageable, MarketplaceLotSpecification specification) {

        return marketplaceLotRepository.findAll(specification, pageable);
    }

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

    public Optional<MarketplaceBet> findCurrentBet(MarketplaceLot lot) {
        return lot.getBets().stream()
                .max(Comparator.comparing(MarketplaceBet::getAmount));
    }

    public MarketplaceBet getCurrentBet(MarketplaceLot lot) {

        return findCurrentBet(lot)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find current bet for lot: " + lot.getId()));
    }

    //todo: change method name
    public void checkLotForDelete(MarketplaceLot lot) {
        if (lot.getIsBuyerHasTaken() && lot.getIsSellerHasTaken()) {
            delete(lot);
        } else {
            save(lot);
        }
    }

}