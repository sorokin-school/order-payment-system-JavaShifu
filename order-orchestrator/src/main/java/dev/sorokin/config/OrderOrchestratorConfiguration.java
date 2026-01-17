package dev.sorokin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class OrderOrchestratorConfiguration {

    @Bean
    public RestTemplate getProperties() {
        return new RestTemplate();
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService getExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}