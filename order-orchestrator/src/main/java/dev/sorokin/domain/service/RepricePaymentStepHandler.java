package dev.sorokin.domain.service;

import dev.sorokin.api.client.WarehouseStubClient;
import dev.sorokin.api.warehouse.CalculatePricingRequestDto;
import dev.sorokin.domain.entity.Order;
import dev.sorokin.domain.entity.PaymentStatus;
import dev.sorokin.domain.entity.PaymentTask;
import dev.sorokin.domain.entity.PaymentTaskStatus;
import dev.sorokin.domain.entity.PaymentTaskStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepricePaymentStepHandler implements PaymentStepHandler {

    private final WarehouseStubClient warehouseStubClient;

    @Override
    public PaymentTaskStep getStep() {
        return PaymentTaskStep.REPRICE;
    }

    @Override
    public void handle(Order order, PaymentTask paymentTask) {
        var repriceRequest = new CalculatePricingRequestDto(order.getId());
        var calculatePricingResponse = warehouseStubClient.repricePayment(repriceRequest);
        var finalAmount = calculatePricingResponse.finalAmount();
        order.setFinalAmount(finalAmount);

        if (finalAmount.compareTo(order.getAuthorizedAmount()) > 0) {
            order.setPaymentStatus(PaymentStatus.PRICE_CHANGED_FAILED);
            order.setFailureReason("Цена товара выросла");
            paymentTask.setStatus(PaymentTaskStatus.FAILED_NON_RETRYABLE);

            return;
        }

        paymentTask.setStep(PaymentTaskStep.CAPTURE);
    }
}