package dev.sorokin.domain.service;

import dev.sorokin.api.payment.AuthorizationStatus;
import dev.sorokin.api.payment.AuthorizePaymentRequestDto;
import dev.sorokin.api.payment.AuthorizePaymentResponseDto;
import dev.sorokin.api.payment.CapturePaymentRequestDto;
import dev.sorokin.api.payment.CapturePaymentResponseDto;
import dev.sorokin.api.warehouse.CalculatePricingRequestDto;
import dev.sorokin.api.warehouse.CalculatePricingResponseDto;
import dev.sorokin.domain.entity.Order;
import dev.sorokin.domain.entity.PaymentStatus;
import dev.sorokin.domain.entity.PaymentTask;
import dev.sorokin.domain.entity.PaymentTaskStatus;
import dev.sorokin.domain.entity.PaymentTaskStep;
import dev.sorokin.domain.repository.OrderRepository;
import dev.sorokin.domain.repository.PaymentTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProcessor {

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final PaymentTaskRepository paymentTaskRepository;

    @Value("${hosts.base-url.payment-stub}")
    private String paymentStubUrl;

    @Value("${hosts.base-url.warehouse-stub}")
    private String warehouseStubUrl;

    @Transactional
    public void paymentTaskProcess(PaymentTask paymentTask) {
        log.info(">-! Task execution: taskId = {}, taskStatus = {}", paymentTask.getId(), paymentTask.getStep());

        var orderId = paymentTask.getOrderId().getId();
        var orderTmp = orderRepository.findById(orderId);

        if (orderTmp.isEmpty()) {
            log.error("Order id {} not found", orderId);

            return;
        }

        var order = orderTmp.get();

        switch (paymentTask.getStep()) {
            case AUTH -> authorizePayment(order, paymentTask);
            case REPRICE -> repricePayment(order, paymentTask);
            case CAPTURE -> capturePayment(order, paymentTask);
            case null, default -> {
                paymentTask.setStatus(PaymentTaskStatus.FAILED_RETRYABLE);
                order.setFailureReason("Неизвестная ошибка");
            }
        }

        paymentTaskRepository.save(paymentTask);

        log.info(">=! Task execution: taskId = {}, taskStatus = {}", paymentTask.getId(), paymentTask.getStep());
    }

    private void authorizePayment(Order order, PaymentTask paymentTask) {
        var authRequest = new AuthorizePaymentRequestDto(order.getCustomerId(), order.getClientEstimate());

        String authRequestUrl = UriComponentsBuilder
                .fromUriString(paymentStubUrl)
                .pathSegment("authorize")
                .toUriString();

        ResponseEntity<AuthorizePaymentResponseDto> authResponse =
                restTemplate.postForEntity(
                        authRequestUrl,
                        authRequest,
                        AuthorizePaymentResponseDto.class
                );

        var authorizePaymentResponse = authResponse.getBody();

        if (authorizePaymentResponse == null) {
            throw new RuntimeException("Сбой авторизации платежа");
        }

        var authStatus = authorizePaymentResponse.status();
        var authorizedAmount = authorizePaymentResponse.authorizedAmount();

        if (AuthorizationStatus.AUTHORIZED.equals(authStatus)) {
            order.setAuthorizedAmount(authorizedAmount);
            order.setPaymentStatus(PaymentStatus.AUTHORIZED);
            paymentTask.setStep(PaymentTaskStep.REPRICE);
            repricePayment(order, paymentTask);
        } else {
            order.setPaymentStatus(PaymentStatus.AUTHORIZATION_FAILED);
            order.setFailureReason("Отказ банка");
            paymentTask.setStatus(PaymentTaskStatus.FAILED_RETRYABLE);
        }
    }

    private void repricePayment(Order order, PaymentTask paymentTask) {
        String repriceRequestUrl = UriComponentsBuilder
                .fromUriString(warehouseStubUrl)
                .pathSegment("calculate-price")
                .toUriString();

        var repriceRequest = new CalculatePricingRequestDto(order.getId());

        ResponseEntity<CalculatePricingResponseDto> repriceResponse =
                restTemplate.postForEntity(
                        repriceRequestUrl,
                        repriceRequest,
                        CalculatePricingResponseDto.class
                );

        var calculatePricingResponse = repriceResponse.getBody();

        if (calculatePricingResponse == null) {
            throw new RuntimeException("Сбой проверки цены");
        }

        var finalAmount = calculatePricingResponse.finalAmount();
        order.setFinalAmount(calculatePricingResponse.finalAmount());

        if (finalAmount.compareTo(order.getAuthorizedAmount()) == 1) {
            order.setPaymentStatus(PaymentStatus.PRICE_CHANGED_FAILED);
            order.setFailureReason("Цена товара выросла");
            paymentTask.setStatus(PaymentTaskStatus.FAILED_NON_RETRYABLE);
        } else {
            paymentTask.setStep(PaymentTaskStep.CAPTURE);
            capturePayment(order, paymentTask);
        }
    }

    private void capturePayment(Order order, PaymentTask paymentTask) {
        var captureRequest = new CapturePaymentRequestDto(order.getAuthorizedAmount(), order.getCustomerId());

        String captureRequestUrl = UriComponentsBuilder
                .fromUriString(paymentStubUrl)
                .pathSegment("capture")
                .toUriString();

        ResponseEntity<CapturePaymentResponseDto> captureResponse =
                restTemplate.postForEntity(
                        captureRequestUrl,
                        captureRequest,
                        CapturePaymentResponseDto.class
                );

        var capturePaymentResponse = captureResponse.getBody();

        if (capturePaymentResponse != null) {
            order.setCapturedAmount(capturePaymentResponse.capturedAmount());
            order.setPaymentStatus(PaymentStatus.SUCCEED_PAID);
            paymentTask.setStatus(PaymentTaskStatus.SUCCEEDED);
        } else {
            throw new RuntimeException("Сбой проведения платежа");
        }
    }
}