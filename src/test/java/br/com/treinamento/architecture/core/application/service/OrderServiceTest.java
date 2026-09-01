package br.com.treinamento.architecture.core.application.service;

import static br.com.treinamento.architecture.support.OrderTestData.ORDER_ID;
import static br.com.treinamento.architecture.support.OrderTestData.order;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.treinamento.architecture.core.application.order.ports.out.OrderRepository;
import br.com.treinamento.architecture.core.domain.order.Orders;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("OrderService - testes unitários")
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService(repository);
    }

    @Test
    @Order(1)
    @DisplayName("deve salvar e devolver o pedido criado")
    void shouldCreateOrder() {
        // Arrange: Cria um pedido de teste
        Orders expected = order();
        when(repository.saveOrder(expected)).thenReturn(expected);
        
        // Act & Assert: Chama o serviço para criar o pedido e verifica se o resultado é o esperado
        assertSame(expected, service.createOrder(expected));
        verify(repository).saveOrder(expected);
    }

    @Test
    @Order(2)
    @DisplayName("deve buscar pedido pelo identificador")
    void shouldGetOrderById() {
        // Arrange: Configura o repositório para retornar um pedido de teste quando solicitado pelo ID
        Orders expected = order();
        when(repository.getOrderById(ORDER_ID)).thenReturn(expected);

        // Act & Assert: Chama o serviço para buscar o pedido pelo ID e verifica se o resultado é o esperado
        assertSame(expected, service.getOrderById(ORDER_ID));
        verify(repository).getOrderById(ORDER_ID);
    }

    @Test
    @Order(3)
    @DisplayName("não deve acessar o repositório quando a entrada for nula")
    void shouldRejectNullInputs() {
        // Act & Assert: Verifica se o serviço lança NullPointerException para entradas nulas e não acessa o repositório
        assertThrows(NullPointerException.class, () -> service.createOrder(null));
        assertThrows(NullPointerException.class, () -> service.getOrderById(null));
        verify(repository, never()).saveOrder(null);
        verify(repository, never()).getOrderById(null);
    }
}
