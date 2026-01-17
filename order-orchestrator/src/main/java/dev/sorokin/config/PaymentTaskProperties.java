package dev.sorokin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "payment-task.poller")
public class PaymentTaskProperties {
    // Сколько читать тасок за один раз
    private int batchSize;

    // Задержка перед повторной вычиткой таски из БД
    private Duration retryDelay;

    // Максимальное количество попыток обработки
    private int maxAttempts;
}