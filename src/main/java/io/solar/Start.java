package io.solar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;


@SpringBootApplication
public class Start {

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(Start.class);
        app.run(args);
    }
}