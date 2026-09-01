package br.com.treinamento.architecture.core.domain.order;

import static br.com.treinamento.architecture.support.OrderTestData.ORDER_ID;
import static br.com.treinamento.architecture.support.OrderTestData.orderItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import br.com.treinamento.architecture.core.domain.exception.QuantityInvalidException;
import br.com.treinamento.architecture.core.domain.exception.SkuEmptyException;
import br.com.treinamento.architecture.core.domain.exception.TotalPriceInvalidException;
import br.com.treinamento.architecture.core.domain.exception.TotalPriceNullException;
import br.com.treinamento.architecture.core.domain.exception.UnitPriceInvalidException;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("OrderItem - testes unitários")
@Label("OrderItem - testes unitários")
class OrderItemTest {

    @Test
    @Order(1)
    @DisplayName("deve calcular e atualizar os valores do item")
    void shouldCalculateAndUpdateItem() {
        OrderItem item = orderItem();
        assertEquals(new BigDecimal("21.00"), item.getTotalPrice());
        item.updateQuantity(3);
        item.updateUnitPrice(new BigDecimal("4.00"));
        item.updateSku("NEW-SKU");
        item.updateTotalPrice(new BigDecimal("10.00"));
        assertEquals("NEW-SKU", item.getSku());
        assertEquals(3, item.getQuantity());
        assertEquals(new BigDecimal("3.33"), item.getUnitPrice());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUpdates")
    @Order(2)
    @DisplayName("deve rejeitar atualizações inválidas")
    void shouldRejectInvalidUpdates(String label, Runnable update, Class<? extends Throwable> type) {
        assertThrows(type, update::run);
    }

    
    @Property(tries = 100)
    @Label("Total do item é preço unitário multiplicado pela quantidade")
    void totalShouldBeUnitPriceTimesQuantity(@ForAll @IntRange(min = 1, max = 1000) int quantity) {
        BigDecimal unitPrice = new BigDecimal("12.34");
        var item = new OrderItem(null, ORDER_ID, "SKU", quantity, unitPrice);
        assertEquals(unitPrice.multiply(BigDecimal.valueOf(quantity)), item.getTotalPrice());
    }

    static Stream<Arguments> invalidUpdates() {
        return Stream.of(
                Arguments.of("quantidade zero", (Runnable) () -> orderItem().updateQuantity(0), QuantityInvalidException.class),
                Arguments.of("preço unitário zero", (Runnable) () -> orderItem().updateUnitPrice(BigDecimal.ZERO), UnitPriceInvalidException.class),
                Arguments.of("SKU vazio", (Runnable) () -> orderItem().updateSku("  "), SkuEmptyException.class),
                Arguments.of("total nulo", (Runnable) () -> orderItem().updateTotalPrice(null), TotalPriceNullException.class),
                Arguments.of("total negativo", (Runnable) () -> orderItem().updateTotalPrice(BigDecimal.valueOf(-1)), TotalPriceInvalidException.class));
    }
}
