package dev.sorokin.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_tasks")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTask {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "order_id", nullable = false)
    private Order orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentTaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "step", nullable = false)
    private PaymentTaskStep step;

    @Column(name = "attempts", nullable = false)
    private Integer attempts;

    @Column(name = "next_attempt_at")
    private OffsetDateTime nextAttemptAt;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        this.status = PaymentTaskStatus.NEW;
        this.step = PaymentTaskStep.AUTH;
        this.attempts = 0;
        this.createdAt = OffsetDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.attempts = this.attempts + 1;
        this.updatedAt = OffsetDateTime.now();
    }
}