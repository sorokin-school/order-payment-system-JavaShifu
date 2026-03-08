package dev.sorokin.domain.service;

import dev.sorokin.api.client.PaymentStubClient;
import dev.sorokin.api.payment.CapturePaymentRequestDto;
import dev.sorokin.domain.entity.Order;
import dev.sorokin.domain.entity.PaymentStatus;
import dev.sorokin.domain.entity.PaymentTask;
import dev.sorokin.domain.entity.PaymentTaskStatus;
import dev.sorokin.domain.entity.PaymentTaskStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CapturePaymentStepHandler implements PaymentStepHandler {

    private final PaymentStubClient paymentStubClient;

    @Override
    public PaymentTaskStep getStep() {
        return PaymentTaskStep.CAPTURE;
    }

    @Override
    public void handle(Order order, PaymentTask paymentTask) {
        var captureRequest = new CapturePaymentRequestDto(order.getAuthorizedAmount(), order.getCustomerId());
        var capturePaymentResponse = paymentStubClient.capturePayment(captureRequest);
        order.setCapturedAmount(capturePaymentResponse.capturedAmount());
        order.setPaymentStatus(PaymentStatus.SUCCEED_PAID);
        paymentTask.setStatus(PaymentTaskStatus.SUCCEEDED);
    }
}