package dev.sorokin.domain.exception;

/**
 * Ошибка при вызове сервиса пересчёта цены склада (пустой ответ или неуспешный HTTP-статус)
 */
public class WarehousePricingException extends ExternalServiceException {
    private static final String OPERATION = "calculate-price";
    private static final String SERVICE_NAME = "warehouse-stub";

    public WarehousePricingException(String message, Integer statusCode) {
        super(message, OPERATION, SERVICE_NAME, statusCode);
    }

    public WarehousePricingException(String message) {
        this(message, null);
    }
}