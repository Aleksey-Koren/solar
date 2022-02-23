package io.solar.service.price;

import io.solar.entity.price.Price;
import io.solar.repository.price.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;

    public Price getById(Long priceId) {

        return priceRepository.findById(priceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find price with id = " + priceId));
    }

    public Optional<Price> findById(Long priceId) {

        return priceRepository.findById(priceId);
    }

    public Price save(Price price) {

        return priceRepository.save(price);
    }
}
