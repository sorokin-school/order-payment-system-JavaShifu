package dev.sorokin.domain.exception;

/**
 * Ошибка при вызове сервиса авторизации платежа (пустой ответ или неуспешный HTTP-статус)
 */
public class PaymentAuthorizationException extends RuntimeException {

    public PaymentAuthorizationException(String message) {
        super(message);
    }
}