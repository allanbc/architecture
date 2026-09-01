package br.com.treinamento.architecture.infrastructure.adapters.in.web.mapper;

import static br.com.treinamento.architecture.support.OrderTestData.CUSTOMER_ID;
import static br.com.treinamento.architecture.support.OrderTestData.ORDER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
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
import org.mapstruct.factory.Mappers;

import br.com.treinamento.architecture.core.domain.enums.OrderStatusEnum;
import br.com.treinamento.architecture.infrastructure.adapters.in.web.dto.RequestOrder;
import br.com.treinamento.architecture.infrastructure.adapters.in.web.dto.RequestOrderItem;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("OrderMapper - testes unitários")
class OrderMapperTest {

    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @Test
    @Order(1)
    @DisplayName("deve mapear a requisição completa para o domínio")
    void shouldMapRequestToDomain() {
        // Cria uma requisição de pedido com itens
        var request = new RequestOrder(CUSTOMER_ID, List.of(
                new RequestOrderItem("A", 2, new BigDecimal("10.00")),
                new RequestOrderItem("B", 3, new BigDecimal("5.50"))));
        
        var result = mapper.toDomain(request, ORDER_ID);

        assertEquals(ORDER_ID, result.getId());
        assertEquals(CUSTOMER_ID, result.getCustumerId());
        assertEquals(OrderStatusEnum.CREATED, result.getStatus());
        assertEquals(new BigDecimal("36.50"), result.getTotal());
        assertNotNull(result.getCreatedAt());
        assertEquals(2, result.getItems().size());
        assertEquals(ORDER_ID, result.getItems().getFirst().getOrderId());
    }

    /**
     * Testa a validação de itens do pedido, garantindo que uma exceção seja lançada quando a lista de itens for nula ou vazia.
     */
    @ParameterizedTest(name = "{index} => itens={0}")
    @MethodSource("invalidItemLists")
    @Order(2)
    @DisplayName("deve rejeitar pedido sem itens")
    void shouldRejectOrderWithoutItems(List<RequestOrderItem> items) {
        var request = new RequestOrder(CUSTOMER_ID, items);

        var exception = assertThrows(IllegalArgumentException.class,
                () -> mapper.toDomain(request, ORDER_ID));

        assertEquals("Order must contain at least one item", exception.getMessage());
    }

    static Stream<Arguments> invalidItemLists() {
        return Stream.of(Arguments.of((Object) null), Arguments.of(List.of()));
    }
}
