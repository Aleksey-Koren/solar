package io.solar.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@EnableConfigurationProperties
@ConfigurationProperties("app")
@Getter
@Setter
public class AppProperties {
    private String goodsGenerationDelayMinutes;
    private String goodsInitialDelayMinutes;
    private Float viewDistanceWithoutRadar;
}