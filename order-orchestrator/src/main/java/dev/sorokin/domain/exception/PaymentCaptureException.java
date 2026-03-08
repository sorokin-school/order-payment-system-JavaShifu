package dev.sorokin.domain.exception;

/**
 * Ошибка при вызове сервиса списания (capture) платежа (пустой ответ или неуспешный HTTP-статус)
 */
public class PaymentCaptureException extends RuntimeException {

    public PaymentCaptureException(String message) {
        super(message);
    }
}