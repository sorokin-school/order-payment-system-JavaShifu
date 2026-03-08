package dev.sorokin.domain.entity;

public enum PaymentTaskStep {
    AUTH,        // Авторизация средств (hold) в платёжном шлюзе

    REPRICE,     // Пересчёт финальной суммы после ответа склада

    CAPTURE,     // Списание средств (capture) по ранее выполненной авторизации
}