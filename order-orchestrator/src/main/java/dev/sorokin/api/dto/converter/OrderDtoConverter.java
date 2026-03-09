package dev.sorokin.api.dto.converter;

import dev.sorokin.api.dto.OrderCreateResponse;
import dev.sorokin.domain.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDtoConverter {

    @Mapping(target = "orderId", source = "id")
    OrderCreateResponse convertToDto(Order order);
}