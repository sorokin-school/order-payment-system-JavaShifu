package dev.sorokin.api.dto.converter;

import dev.sorokin.api.dto.OrderCreateResponse;
import dev.sorokin.domain.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderDtoConverter {
    public OrderCreateResponse convertToDto(Order order) {
        OrderCreateResponse.OrderCreateResponseBuilder builder = OrderCreateResponse.builder()
                .orderId(order.getId())
                .address(order.getAddress())
                .clientEstimate(order.getClientEstimate())
                .finalAmount(order.getFinalAmount())
                .capturedAmount(order.getCapturedAmount())
                .paymentStatus(order.getPaymentStatus());

        if (order.getFailureReason() != null) {
            builder.failureReason(order.getFailureReason());
        }

        return builder.build();
    }
}