package io.solar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Start {

    public static void main(String[] args) {

        SpringApplication.run(Start.class, args);
    }

    //TODO add to all CRUD services  [Object getById(long id)] methods,
    // where Optional<Object> findById(Long id).orElseThrow(new ResponseStatusException) will be used
    // to decrease boiler plate code quantity
}