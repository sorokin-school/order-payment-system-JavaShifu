package dev.sorokin.domain.entity;

public enum PaymentTaskStatus {
    NEW,                 // Задача создана, ещё не обрабатывалась

    IN_PROGRESS,         // Задача взята воркером в работу

    SUCCEEDED,           // Задача выполнена успешно

    FAILED_RETRYABLE,    // Ошибка, допускающая повторные попытки

    FAILED_NON_RETRYABLE // Фатальная ошибка, ретраи запрещены
}