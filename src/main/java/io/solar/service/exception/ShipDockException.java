package io.solar.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ShipDockException extends ResponseStatusException {

    public ShipDockException() {
        super(HttpStatus.CONFLICT, "Cannot dock ship to station");
    }

}
