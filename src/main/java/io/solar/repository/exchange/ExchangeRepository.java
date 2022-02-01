package io.solar.repository.exchange;

import io.solar.entity.User;
import io.solar.entity.exchange.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    Optional<Exchange> findByFirstUserOrSecondUser(User user);

}
