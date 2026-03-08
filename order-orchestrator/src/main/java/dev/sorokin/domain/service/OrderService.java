package dev.sorokin.domain.service;

import dev.sorokin.api.dto.OrderCreateRequest;
import dev.sorokin.domain.entity.Order;
import dev.sorokin.domain.entity.PaymentTask;
import dev.sorokin.domain.repository.OrderRepository;
import dev.sorokin.domain.repository.PaymentTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentTaskRepository paymentTaskRepository;

    @Transactional
    public Order createOrder(OrderCreateRequest requestDto) {
        var orderEntity = Order.builder()
                .address(requestDto.address())
                .build();

        var savedOrderEntity = orderRepository.save(orderEntity);

        var paymentTask = PaymentTask.builder()
                .order(savedOrderEntity)
                .build();

        paymentTaskRepository.save(paymentTask);

        return savedOrderEntity;
    }

    public Optional<Order> findOrder(UUID id) {
        return orderRepository.findById(id);
    }
}