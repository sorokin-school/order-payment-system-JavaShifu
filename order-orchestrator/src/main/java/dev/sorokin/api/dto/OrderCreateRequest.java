package dev.sorokin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrderCreateRequest(
        @NotBlank(message = "address must not be blank")
        @Size(max = 500)
        String address
) { }