package io.solar.repository.marketplace;

import io.solar.entity.marketplace.MarketplaceLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketplaceLotRepository extends JpaRepository<MarketplaceLot, Long> {
}