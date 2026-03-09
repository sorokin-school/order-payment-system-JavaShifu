package dev.sorokin.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties
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
}