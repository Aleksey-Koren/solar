package io.solar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.io.IOException;


@SpringBootApplication
@EnableScheduling
public class Start {

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(Start.class);
        app.run(args);
    }
}