package io.solar.service.exchange;

import io.solar.repository.exchange.ExchangeOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeOfferService {

    private final ExchangeOfferRepository exchangeOfferRepository;


}
