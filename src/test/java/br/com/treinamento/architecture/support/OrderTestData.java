package br.com.treinamento.architecture.support;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.treinamento.architecture.core.domain.enums.OrderStatusEnum;
import br.com.treinamento.architecture.core.domain.order.OrderItem;
import br.com.treinamento.architecture.core.domain.order.Orders;
import br.com.treinamento.architecture.infrastructure.adapters.in.web.dto.RequestOrder;
import br.com.treinamento.architecture.infrastructure.adapters.in.web.dto.RequestOrderItem;

public final class OrderTestData {

    public static final UUID ORDER_ID = UUID.fromString("4ae6a669-6144-4c2a-8148-bf8b8ee77df7");
    public static final UUID CUSTOMER_ID = UUID.fromString("696b3724-e2aa-4494-9f3f-b5bf36c4c205");

    private OrderTestData() {
    }

    public static RequestOrderItem requestItem() {
        return new RequestOrderItem("SKU-1", 2, new BigDecimal("10.50"));
    }

    public static RequestOrder requestOrder() {
        return new RequestOrder(CUSTOMER_ID, List.of(requestItem()));
    }

    public static OrderItem orderItem() {
        return new OrderItem(null, ORDER_ID, "SKU-1", 2, new BigDecimal("10.50"));
    }

    public static Orders order() {
        return new Orders(ORDER_ID, CUSTOMER_ID, OrderStatusEnum.CREATED,
                new BigDecimal("21.00"), Instant.parse("2026-01-01T10:00:00Z"), List.of(orderItem()));
    }
}
