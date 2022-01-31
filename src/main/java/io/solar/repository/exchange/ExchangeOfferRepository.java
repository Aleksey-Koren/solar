package io.solar.repository.exchange;

import io.solar.entity.exchange.ExchangeOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeOfferRepository extends JpaRepository<ExchangeOffer, Long> {
}
