package dev.sorokin.domain.service;

import dev.sorokin.config.PaymentTaskProperties;
import dev.sorokin.domain.entity.PaymentTask;
import dev.sorokin.domain.entity.PaymentTaskStatus;
import dev.sorokin.domain.repository.PaymentTaskRepository;
import dev.sorokin.utils.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaymentTaskDispatcher {

    private final OrderProcessor orderProcessor;
    private final ExecutorService executorService;
    private final PaymentTaskProperties paymentTaskProperties;
    private final PaymentTaskRepository paymentTaskRepository;

    public void dispatch(PaymentTask paymentTask) {
        CompletableFuture
                .runAsync(() -> orderProcessor.paymentTaskProcess(paymentTask), executorService)
                .exceptionally(exception -> {
                            handlePaymentTaskException(paymentTask, exception);

                            return null;
                        }
                );
    }

    public void handlePaymentTaskException(PaymentTask paymentTask, Throwable exception) {
        log.error("Payment task id = {} failed with unexpected exception: {}",
                paymentTask.getId(), ExceptionUtil.getStackTraceWithMessage(exception));

        schedulePaymentTaskRetry(paymentTask);
    }

    public void schedulePaymentTaskRetry(PaymentTask paymentTask) {
        log.info("Scheduling default retry for payment task id = {}", paymentTask.getId());

        paymentTaskRepository.save(paymentTask.toBuilder()
                .nextAttemptAt(OffsetDateTime.now().plus(paymentTaskProperties.getRetryDelay()))
                .status(PaymentTaskStatus.FAILED_RETRYABLE)
                .build());
    }
}