package br.com.treinamento.architecture.infrastructure.adapters.in.web;

import static br.com.treinamento.architecture.support.OrderTestData.ORDER_ID;
import static br.com.treinamento.architecture.support.OrderTestData.order;
import static br.com.treinamento.architecture.support.OrderTestData.requestOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.treinamento.architecture.core.application.order.ports.in.OrderUseCase;
import br.com.treinamento.architecture.core.domain.order.Orders;
import br.com.treinamento.architecture.infrastructure.adapters.in.web.controller.OrderController;
import br.com.treinamento.architecture.infrastructure.adapters.in.web.mapper.OrderMapper;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("OrderController - testes unitários")
class OrderControllerTest {

    @Mock private OrderUseCase useCase;
    @Mock private OrderMapper mapper;
    private OrderController controller;

    /**
     * Configura o contexto de teste antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        controller = new OrderController(useCase, mapper);
    }

    /**
     * Limpa o contexto de requisição após cada teste para evitar interferência entre testes.
     */
    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * Testa a criação de um pedido e verifica se o status HTTP retornado é 201 (Created) e se o cabeçalho Location está correto.
     */
    @Test
    @Order(1)
    @DisplayName("deve criar pedido e retornar 201 com Location")
    void shouldCreateOrder() {
        var request = requestOrder();
        Orders mapped = order();

        // Configura um MockHttpServletRequest para simular uma requisição HTTP
        var servletRequest = new MockHttpServletRequest("POST", "/orders");
        servletRequest.setScheme("http");
        servletRequest.setServerName("localhost");
        servletRequest.setServerPort(8080);

        // Configura o contexto de requisição para simular uma requisição HTTP
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));
        when(mapper.toDomain(request)).thenReturn(mapped);
        when(useCase.createOrder(mapped)).thenReturn(mapped);

        var response = controller.createOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(mapped, response.getBody());
        assertEquals("http://localhost:8080/orders/" + ORDER_ID, response.getHeaders().getLocation().toString());
        // Verifica se os métodos do mapper e do use case foram chamados corretamente
        verify(mapper).toDomain(request);
        verify(useCase).createOrder(mapped);
    }

    /**
     * Testa a busca de um pedido por ID e verifica se o status HTTP retornado é 200 (OK) e se o corpo da resposta contém o pedido esperado.
     */
    @Test
    @Order(2)
    @DisplayName("deve buscar pedido e retornar 200")
    void shouldGetOrderById() {
        Orders expected = order();
        when(useCase.getOrderById(ORDER_ID)).thenReturn(expected);

        var response = controller.getOrderById(ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(useCase).getOrderById(ORDER_ID);
    }
}
