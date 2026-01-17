package dev.sorokin.api.dto;

import dev.sorokin.domain.entity.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderCreateResponse(
        UUID orderId,
        String address,
        BigDecimal clientEstimate,
        BigDecimal finalAmount,
        BigDecimal capturedAmount,
        PaymentStatus paymentStatus,
        String failureReason
) { }