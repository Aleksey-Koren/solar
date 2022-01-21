package io.solar.repository.marketplace;

import io.solar.entity.marketplace.LotBet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotBetRepository extends JpaRepository<LotBet, Long> {
}