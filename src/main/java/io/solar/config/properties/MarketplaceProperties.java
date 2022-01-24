package io.solar.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("app.marketplace")
@Getter
@Setter
public class MarketplaceProperties {
    private Byte betStepPercents;
    private Byte commissionPercent;
}
