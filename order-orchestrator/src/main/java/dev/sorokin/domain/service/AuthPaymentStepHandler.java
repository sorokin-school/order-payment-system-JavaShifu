package dev.sorokin.domain.service;

import dev.sorokin.api.client.PaymentStubClient;
import dev.sorokin.api.payment.AuthorizationStatus;
import dev.sorokin.api.payment.AuthorizePaymentRequestDto;
import dev.sorokin.domain.entity.Order;
import dev.sorokin.domain.entity.PaymentStatus;
import dev.sorokin.domain.entity.PaymentTask;
import dev.sorokin.domain.entity.PaymentTaskStatus;
import dev.sorokin.domain.entity.PaymentTaskStep;
import dev.sorokin.domain.exception.PaymentAuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthPaymentStepHandler implements PaymentStepHandler {
    private final PaymentStubClient paymentStubClient;

    @Override
    public PaymentTaskStep getStep() {
        return PaymentTaskStep.AUTH;
    }

    @Override
    public void handle(Order order, PaymentTask paymentTask) {
        var authRequest = new AuthorizePaymentRequestDto(order.getCustomerId(), order.getClientEstimate());

        try {
            var authorizePaymentResponse = paymentStubClient.authorizePayment(authRequest);
            var authStatus = authorizePaymentResponse.status();
            var authorizedAmount = authorizePaymentResponse.authorizedAmount();

            if (!AuthorizationStatus.AUTHORIZED.equals(authStatus)) {
                order.setPaymentStatus(PaymentStatus.AUTHORIZATION_FAILED);
                order.setFailureReason("Отказ банка");
                paymentTask.setStatus(PaymentTaskStatus.FAILED_RETRYABLE);

                return;
            }

            order.setAuthorizedAmount(authorizedAmount);
            order.setPaymentStatus(PaymentStatus.AUTHORIZED);
            paymentTask.setStep(PaymentTaskStep.REPRICE);
        } catch (PaymentAuthorizationException ex) {
            order.setFailureReason("Сбой авторизации платежа");
            paymentTask.setStatus(PaymentTaskStatus.FAILED_RETRYABLE);
        }
    }
}