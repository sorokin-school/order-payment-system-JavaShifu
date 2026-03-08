package dev.sorokin.domain.exception;

/**
 * Ошибка при вызове сервиса списания (capture) платежа (пустой ответ или неуспешный HTTP-статус).
 */
public class PaymentCaptureException extends ExternalServiceException {
    private static final String OPERATION = "capture";
    private static final String SERVICE_NAME = "payment-stub";

    public PaymentCaptureException(String message, Integer statusCode) {
        super(message, OPERATION, SERVICE_NAME, statusCode);
    }

    public PaymentCaptureException(String message) {
        this(message, null);
    }
}