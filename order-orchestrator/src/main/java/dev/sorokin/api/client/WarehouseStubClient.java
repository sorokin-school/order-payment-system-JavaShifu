package dev.sorokin.api.client;

import dev.sorokin.api.warehouse.CalculatePricingRequestDto;
import dev.sorokin.api.warehouse.CalculatePricingResponseDto;
import dev.sorokin.domain.exception.WarehousePricingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class WarehouseStubClient {
    private final RestTemplate restTemplate;

    @Value("${hosts.base-url.warehouse-stub}")
    private String warehouseStubBaseUrl;

    public WarehouseStubClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CalculatePricingResponseDto repricePayment(CalculatePricingRequestDto request) {
        String url = UriComponentsBuilder
                .fromUriString(warehouseStubBaseUrl)
                .pathSegment("calculate-price")
                .toUriString();
        var response = restTemplate
                .postForEntity(url, request, CalculatePricingResponseDto.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new WarehousePricingException("Calculate price failed: HTTP " + response.getStatusCode());
        }

        CalculatePricingResponseDto body = response.getBody();

        if (body == null) {
            throw new WarehousePricingException("Сбой проверки цены: пустой ответ");
        }

        return body;
    }
}