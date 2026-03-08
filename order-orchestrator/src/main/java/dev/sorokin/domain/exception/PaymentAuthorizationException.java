package dev.sorokin.domain.exception;

/**
 * Ошибка при вызове сервиса авторизации платежа (пустой ответ или неуспешный HTTP-статус).
 */
public class PaymentAuthorizationException extends ExternalServiceException {
    private static final String OPERATION = "authorize";
    private static final String SERVICE_NAME = "payment-stub";

    public PaymentAuthorizationException(String message, Integer statusCode) {
        super(message, OPERATION, SERVICE_NAME, statusCode);
    }

    public PaymentAuthorizationException(String message) {
        this(message, null);
    }
}