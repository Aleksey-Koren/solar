package io.solar.repository.price;

import io.solar.entity.price.PriceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceProductRepository extends JpaRepository<PriceProduct, Long> {

    Optional<PriceProduct> findByProductIdAndPriceId(Long productId, Long priceId);

    void deleteAllByPriceIdAndIdNotIn(Long priceId, List<Long> priceProductIds);
}
