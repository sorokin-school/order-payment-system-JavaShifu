package dev.sorokin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Настройки хостов
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "hosts")
public class OrderOrchestratorHostsProperties {

    private BaseUrl baseUrl;

    @Getter
    @Setter
    public static class BaseUrl {
        private String paymentStub;
        private String warehouseStub;
    }
}