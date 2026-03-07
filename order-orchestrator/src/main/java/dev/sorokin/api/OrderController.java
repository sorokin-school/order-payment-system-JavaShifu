package dev.sorokin.api;

import dev.sorokin.api.dto.OrderCreateRequest;
import dev.sorokin.api.dto.OrderCreateResponse;
import dev.sorokin.api.dto.converter.OrderDtoConverter;
import dev.sorokin.domain.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final OrderDtoConverter orderDtoConverter;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody @Valid OrderCreateRequest orderCreateRequest) {
        log.info("Received request to create order: request={}", orderCreateRequest);

        var created = orderService.createOrder(orderCreateRequest);

        log.info("Created order: created={}", created);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderDtoConverter.convertToDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderCreateResponse> getOrder(@PathVariable UUID id) {
        log.info("Getting order with orderId {}", id);

        var foundOrder = orderService.findOrder(id);

        return foundOrder
                .map(orderDtoConverter::convertToDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}