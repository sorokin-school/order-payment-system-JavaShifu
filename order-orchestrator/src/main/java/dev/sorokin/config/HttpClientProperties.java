package dev.sorokin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "http-client")
public class HttpClientProperties {
    private Duration connectTimeout;
    private Duration readTimeout;
}