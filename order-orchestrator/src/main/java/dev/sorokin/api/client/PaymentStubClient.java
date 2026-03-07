package dev.sorokin.api.client;

import dev.sorokin.api.payment.AuthorizePaymentRequestDto;
import dev.sorokin.api.payment.AuthorizePaymentResponseDto;
import dev.sorokin.api.payment.CapturePaymentRequestDto;
import dev.sorokin.api.payment.CapturePaymentResponseDto;
import dev.sorokin.domain.exception.PaymentAuthorizationException;
import dev.sorokin.domain.exception.PaymentCaptureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PaymentStubClient {
    private final RestTemplate restTemplate;

    @Value("${hosts.base-url.payment-stub}")
    private String paymentStubBaseUrl;

    public PaymentStubClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AuthorizePaymentResponseDto authorizePayment(AuthorizePaymentRequestDto request) {
        String url = UriComponentsBuilder
                .fromUriString(paymentStubBaseUrl)
                .pathSegment("authorize")
                .toUriString();
        ResponseEntity<AuthorizePaymentResponseDto> response = restTemplate
                .postForEntity(url, request, AuthorizePaymentResponseDto.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new PaymentAuthorizationException("Authorize failed: HTTP " + response.getStatusCode());
        }

        AuthorizePaymentResponseDto body = response.getBody();

        if (body == null) {
            throw new PaymentAuthorizationException("Сбой авторизации платежа: пустой ответ");
        }

        return body;
    }

    public CapturePaymentResponseDto capturePayment(CapturePaymentRequestDto request) {
        String url = UriComponentsBuilder
                .fromUriString(paymentStubBaseUrl)
                .pathSegment("capture")
                .toUriString();
        ResponseEntity<CapturePaymentResponseDto> response = restTemplate
                .postForEntity(url, request, CapturePaymentResponseDto.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new PaymentCaptureException("Capture failed: HTTP " + response.getStatusCode());
        }

        CapturePaymentResponseDto body = response.getBody();

        if (body == null) {
            throw new PaymentCaptureException("Сбой проведения платежа: пустой ответ");
        }

        return body;
    }
}