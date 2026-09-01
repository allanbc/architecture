package br.com.treinamento.architecture.core.domain.order;

import static br.com.treinamento.architecture.support.OrderTestData.CUSTOMER_ID;
import static br.com.treinamento.architecture.support.OrderTestData.ORDER_ID;
import static br.com.treinamento.architecture.support.OrderTestData.order;
import static br.com.treinamento.architecture.support.OrderTestData.orderItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import br.com.treinamento.architecture.core.domain.enums.OrderStatusEnum;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.BigRange;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Orders - testes unitários")
@Label("Orders - testes unitários")
class OrdersTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidOrders")
    @Order(1)
    @DisplayName("deve validar os dados obrigatórios")
    void shouldRejectInvalidState(String label, Object customerId, OrderStatusEnum status,
            BigDecimal total, Instant createdAt, List<OrderItem> items, Class<? extends Throwable> type) {
        assertThrows(type, () -> new Orders(ORDER_ID, (java.util.UUID) customerId, status, total, createdAt, items));
    }

    @Test
    @Order(2)
    @DisplayName("deve alterar itens, status e total preservando encapsulamento")
    void shouldMutateThroughDomainMethods() {
        Orders orders = order();
        OrderItem newItem = new OrderItem(null, ORDER_ID, "SKU-2", 1, BigDecimal.ONE);

        orders.addItem(newItem);
        orders.updateStatus(OrderStatusEnum.CONFIRMED);
        orders.updateTotal(new BigDecimal("22.00"));

        assertEquals(OrderStatusEnum.CONFIRMED, orders.getStatus());
        assertEquals(new BigDecimal("22.00"), orders.getTotal());
        assertEquals(2, orders.getItems().size());
        assertThrows(UnsupportedOperationException.class, () -> orders.getItems().clear());
        orders.removeItem(newItem);
        orders.cancel();
        assertEquals(OrderStatusEnum.CANCELLED, orders.getStatus());
        assertEquals(1, orders.getItems().size());
    }

    @Property(tries = 50)
    @Label("Todo total não negativo é aceito")
    void shouldAcceptAnyNonNegativeTotal(@ForAll @BigRange(min = "0.00", max = "1000000.00") BigDecimal total) {
        Orders orders = order();
        orders.updateTotal(total);
        assertEquals(total, orders.getTotal());
        assertTrue(orders.getTotal().signum() >= 0);
    }

    static Stream<Arguments> invalidOrders() {
        Instant now = Instant.parse("2026-01-01T10:00:00Z");
        return Stream.of(
                Arguments.of("cliente nulo", null, OrderStatusEnum.CREATED, BigDecimal.ONE, now, List.of(orderItem()), NullPointerException.class),
                Arguments.of("status nulo", CUSTOMER_ID, null, BigDecimal.ONE, now, List.of(orderItem()), NullPointerException.class),
                Arguments.of("total nulo", CUSTOMER_ID, OrderStatusEnum.CREATED, null, now, List.of(orderItem()), NullPointerException.class),
                Arguments.of("total negativo", CUSTOMER_ID, OrderStatusEnum.CREATED, BigDecimal.valueOf(-1), now, List.of(orderItem()), IllegalArgumentException.class),
                Arguments.of("data nula", CUSTOMER_ID, OrderStatusEnum.CREATED, BigDecimal.ONE, null, List.of(orderItem()), NullPointerException.class),
                Arguments.of("itens nulos", CUSTOMER_ID, OrderStatusEnum.CREATED, BigDecimal.ONE, now, null, NullPointerException.class));
    }
}
