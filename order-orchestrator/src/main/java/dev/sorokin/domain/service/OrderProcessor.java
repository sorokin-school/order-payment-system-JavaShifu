package dev.sorokin.domain.service;

import dev.sorokin.domain.entity.PaymentTask;
import dev.sorokin.domain.entity.PaymentTaskStatus;
import dev.sorokin.domain.entity.PaymentTaskStep;
import dev.sorokin.domain.repository.OrderRepository;
import dev.sorokin.domain.repository.PaymentTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OrderProcessor {

    private final OrderRepository orderRepository;
    private final PaymentTaskRepository paymentTaskRepository;
    private final Map<PaymentTaskStep, PaymentStepHandler> paymentStepHandlers;

    public OrderProcessor(
            OrderRepository orderRepository,
            PaymentTaskRepository paymentTaskRepository,
            List<PaymentStepHandler> handlers
    ) {
        this.orderRepository = orderRepository;
        this.paymentTaskRepository = paymentTaskRepository;
        this.paymentStepHandlers = new EnumMap<>(PaymentTaskStep.class);

        for (PaymentStepHandler handler : handlers) {
            this.paymentStepHandlers.put(handler.getStep(), handler);
        }
    }

    @Transactional
    public void paymentTaskProcess(PaymentTask paymentTaskFromPoller) {
        var task = paymentTaskRepository.findById(paymentTaskFromPoller.getId())
                .orElseThrow(() -> new IllegalStateException("PaymentTask not found: " + paymentTaskFromPoller.getId()));

        log.info(">-! Task execution: taskId = {}, taskStatus = {}", task.getId(), task.getStep());

        var orderId = task.getOrder().getId();
        var orderOpt = orderRepository.findById(orderId);

        if (orderOpt.isEmpty()) {
            log.error("Order id {} not found", orderId);

            return;
        }

        var order = orderOpt.get();

        PaymentTaskStep currentStep = task.getStep();

        while (currentStep != null) {
            var handler = paymentStepHandlers.get(currentStep);

            if (handler == null) {
                task.setStatus(PaymentTaskStatus.FAILED_RETRYABLE);
                order.setFailureReason("Неизвестная ошибка");

                break;
            }

            var previousStep = task.getStep();

            handler.handle(order, task);

            if (task.getStatus() == PaymentTaskStatus.FAILED_NON_RETRYABLE
                    || task.getStatus() == PaymentTaskStatus.FAILED_RETRYABLE
                    || task.getStatus() == PaymentTaskStatus.SUCCEEDED) {
                break;
            }

            var nextStep = task.getStep();

            if (nextStep == null || nextStep == previousStep) {
                break;
            }

            currentStep = nextStep;
        }

        paymentTaskRepository.save(task);

        log.info(">=! Task execution: taskId = {}, taskStatus = {}", task.getId(), task.getStep());
    }
}