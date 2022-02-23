package io.solar.repository.price;

import io.solar.entity.price.PriceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceProductRepository extends JpaRepository<PriceProduct, Long> {
}
