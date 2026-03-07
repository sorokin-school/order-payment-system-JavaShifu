package dev.sorokin.domain.service;

import dev.sorokin.domain.entity.Order;
import dev.sorokin.domain.entity.PaymentTask;
import dev.sorokin.domain.entity.PaymentTaskStep;

public interface PaymentStepHandler {
    PaymentTaskStep getStep();
    void handle(Order order, PaymentTask task);
}