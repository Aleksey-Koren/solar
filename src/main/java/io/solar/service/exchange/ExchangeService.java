package io.solar.service.exchange;

import io.solar.entity.User;
import io.solar.entity.exchange.Exchange;
import io.solar.repository.exchange.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;

    public Exchange getById(Long id) {
        return exchangeRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no %s object with id = %d in database", Exchange.class.getSimpleName(), id)
                ));
    }

    public Optional<Exchange> findByUser(User user) {

        return exchangeRepository.findByFirstUserOrSecondUser(user, user);
    }

    public Exchange getByUser(User user) {

        return findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find exchange with user_id = " + user.getId()));
    }

    public Exchange save(Exchange exchange) {
        return exchangeRepository.save(exchange);
    }

    public void delete(Exchange exchange) {
        exchangeRepository.delete(exchange);
    }
}
