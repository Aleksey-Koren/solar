package io.solar.service.exchange;

import io.solar.entity.exchange.ExchangeOffer;
import io.solar.repository.exchange.ExchangeOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ExchangeOfferService {

    private final ExchangeOfferRepository exchangeOfferRepository;

    public ExchangeOffer getById(Long offerId) {

        return exchangeOfferRepository.findById(offerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find offer with id = " + offerId));
    }

    public void delete(ExchangeOffer offer) {

        exchangeOfferRepository.delete(offer);
    }
}
