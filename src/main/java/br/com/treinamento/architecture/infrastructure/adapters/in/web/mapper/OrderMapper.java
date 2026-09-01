package br.com.treinamento.architecture.infrastructure.adapters.in.web.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.treinamento.architecture.core.domain.order.OrderItem;
import br.com.treinamento.architecture.core.domain.order.Orders;
import br.com.treinamento.architecture.infrastructure.adapters.in.web.dto.RequestOrder;
import br.com.treinamento.architecture.infrastructure.adapters.in.web.dto.RequestOrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", source = "orderId")
    @Mapping(target = "custumerId", source = "request.customerId")
    @Mapping(target = "status", expression = "java(OrderStatusEnum.CREATED)")
    @Mapping(target = "total", expression = "java(calculateTotal(request.items()))")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "items", expression = "java(toItems(request.items(), orderId))")
    Orders toDomain(RequestOrder request, UUID orderId);

    default Orders toDomain(RequestOrder request) {
        return toDomain(request, UUID.randomUUID());
    }

    default List<OrderItem> toItems(List<RequestOrderItem> items, UUID orderId) {

        if (items == null || items.isEmpty()) {
                throw new IllegalArgumentException("Order must contain at least one item");
            }

        return items.stream()
                .map(item -> toDomain(item, orderId))
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", source = "orderId")
    OrderItem toDomain(RequestOrderItem item, UUID orderId);

    default BigDecimal calculateTotal(List<RequestOrderItem> items) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        return items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
