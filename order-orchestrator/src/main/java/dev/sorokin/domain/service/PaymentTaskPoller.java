package dev.sorokin.domain.service;

import dev.sorokin.config.PaymentTaskProperties;
import dev.sorokin.domain.entity.PaymentTask;
import dev.sorokin.domain.entity.PaymentTaskStatus;
import dev.sorokin.domain.repository.PaymentTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTaskPoller {

    private final PaymentTaskDispatcher paymentTaskDispatcher;
    private final PaymentTaskProperties paymentTaskProperties;
    private final PaymentTaskRepository paymentTaskRepository;

    public List<PaymentTask> pickTasksForProcessing() {
        log.info("Start polling tasks");

        return paymentTaskRepository.findByStatusWithLimit(
                PaymentTaskStatus.NEW.toString(),
                PaymentTaskStatus.FAILED_RETRYABLE.toString(),
                paymentTaskProperties.getBatchSize()
        );
    }


    @Scheduled(fixedDelayString = "${payment-task.poller.poll-interval}")
    public void poll() {
        var tasks = pickTasksForProcessing();

        if (tasks.isEmpty()) {
            return;
        } else {
            var tasksIds = tasks.stream()
                    .map(PaymentTask::getId)
                    .toList();

            log.info("Successfully picked tasks: count = {}, ids = {}", tasksIds.size(), tasksIds);
        }

        for (var task : tasks) {
            if (task.getAttempts() < paymentTaskProperties.getMaxAttempts()) {
                task.setStatus(PaymentTaskStatus.IN_PROGRESS);
                task.setAttempts(task.getAttempts() == null ? 1 : task.getAttempts() + 1);
                task.setNextAttemptAt(OffsetDateTime.now().plus(paymentTaskProperties.getRetryDelay()));
                paymentTaskRepository.save(task);
                paymentTaskDispatcher.dispatch(task);
            } else {
                log.error("Maximum number of retries reached: taskId = {}", task.getId());

                task.setStatus(PaymentTaskStatus.FAILED_NON_RETRYABLE);
                paymentTaskRepository.save(task);
            }
        }
    }
}