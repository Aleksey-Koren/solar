package io.solar.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
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
    private Integer timeFlowModifier;
    private Integer basicHangarSize;
}