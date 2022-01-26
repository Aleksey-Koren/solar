package io.solar.repository.marketplace;

import io.solar.entity.marketplace.MarketplaceBet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketplaceBetRepository extends JpaRepository<MarketplaceBet, Long> {
}