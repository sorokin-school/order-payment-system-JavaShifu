package dev.sorokin.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "address")
    private String address;

    @Column(name = "client_estimate", nullable = false)
    private BigDecimal clientEstimate;

    @Column(name = "final_amount")
    private BigDecimal finalAmount;

    @Column(name = "authorized_amount")
    private BigDecimal authorizedAmount;

    @Column(name = "captured_amount")
    private BigDecimal capturedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "failure_reason")
    private String failureReason;

    @PrePersist
    private void prePersist() {
        this.customerId = UUID.randomUUID();
        this.clientEstimate = BigDecimal
                .valueOf(100 + Math.random() * 9900)
                .setScale(2, RoundingMode.HALF_UP);
        this.paymentStatus = PaymentStatus.NEW;
    }
}