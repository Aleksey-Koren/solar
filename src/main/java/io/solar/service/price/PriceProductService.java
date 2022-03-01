package io.solar.service.price;

import io.solar.entity.price.PriceProduct;
import io.solar.repository.price.PriceProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceProductService {

    private final PriceProductRepository priceProductRepository;

    public PriceProduct getById(Long priceProductId) {

        return priceProductRepository.findById(priceProductId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find price product with id = " + priceProductId));
    }

    public Optional<PriceProduct> findById(Long priceProductId) {

        return priceProductRepository.findById(priceProductId);
    }

    public Optional<PriceProduct> findByPriceIdAndProductId(Long priceId, Long productId) {

        return priceProductRepository.findByProductIdAndPriceId(productId, priceId);
    }

    public List<PriceProduct> saveAll(List<PriceProduct> priceProducts) {
        return priceProductRepository.saveAll(priceProducts);
    }

    public void deleteAllByPriceIdExceptExisting(Long priceId, List<Long> priceProductIds) {
        priceProductRepository.deleteAllByPriceIdAndIdNotIn(priceId, priceProductIds);
    }
}