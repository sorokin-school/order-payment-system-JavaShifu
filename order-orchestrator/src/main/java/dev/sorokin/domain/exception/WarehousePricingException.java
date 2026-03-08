package dev.sorokin.domain.exception;

/**
 * Ошибка при вызове сервиса пересчёта цены склада (пустой ответ или неуспешный HTTP-статус)
 */
public class WarehousePricingException extends RuntimeException {

    public WarehousePricingException(String message) {
        super(message);
    }
}