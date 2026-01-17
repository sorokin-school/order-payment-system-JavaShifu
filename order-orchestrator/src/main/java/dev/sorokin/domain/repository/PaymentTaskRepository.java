package dev.sorokin.domain.repository;

import dev.sorokin.domain.entity.PaymentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaymentTaskRepository extends JpaRepository<PaymentTask, UUID> {

    @Query(value = """
            SELECT *
            FROM payment_tasks
            WHERE (status = :newStatus OR status = :failedRetryableStatus)
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """,
            nativeQuery = true)
    List<PaymentTask> findByStatusWithLimit(
            @Param("newStatus") String newStatus,
            @Param("failedRetryableStatus") String failedRetryableStatus,
            @Param("batchSize") int batchSize
    );
}