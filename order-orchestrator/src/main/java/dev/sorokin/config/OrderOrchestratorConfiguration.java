package dev.sorokin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(OrderOrchestratorConfiguration.HttpClientProperties.class)
public class OrderOrchestratorConfiguration {

    @Bean
    public RestTemplate getRestTemplate(HttpClientProperties httpClientProperties) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(httpClientProperties.getConnectTimeout());
        factory.setReadTimeout(httpClientProperties.getReadTimeout());

        return new RestTemplate(factory);
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService getExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "http-client")
    public static class HttpClientProperties {
        private Duration connectTimeout;
        private Duration readTimeout;
    }
}