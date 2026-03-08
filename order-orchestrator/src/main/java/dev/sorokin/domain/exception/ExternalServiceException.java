package dev.sorokin.domain.exception;

import lombok.Getter;

/**
 * Базовое проверяемое исключение для ошибок при вызове внешних сервисов.
 * Хранит минимальный набор технической информации для логов и анализа инцидентов.
 */
@Getter
public abstract class ExternalServiceException extends Exception {
    private final String operation;
    private final String serviceName;
    private final Integer httpStatusCode;

    protected ExternalServiceException(
            String message,
            String operation,
            String serviceName,
            Integer httpStatusCode
    ) {
        super(message);
        this.serviceName = serviceName;
        this.operation = operation;
        this.httpStatusCode = httpStatusCode;
    }
}