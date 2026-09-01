package br.com.treinamento.architecture.infrastructure.adapters.in.web.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import br.com.treinamento.architecture.core.domain.exception.SkuEmptyException;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("OrderManagementHandler - testes unitários")
class OrderManagementHandlerTest {

    private OrderManagementHandler handler;

    @BeforeEach
    void setUp() {
        handler = new OrderManagementHandler(
                new HandleValidationRetrieveFields(), new HandleInvalidFormatRetrieveFields());
    }

    @Test
    @Order(1)
    @DisplayName("deve converter exceção de domínio em resposta 400")
    void shouldHandleDomainException() {
        var response = handler.handleOrderManagementException(new SkuEmptyException());
        var body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(400, body.httpStatusCode());
        assertEquals("SKU_EMPTY", body.errorCode());
        assertEquals("Sku cannot be empty", body.message());
        assertEquals("Sku cannot be empty", body.fields().get("sku"));
    }

    @Test
    @Order(2)
    @DisplayName("deve converter RuntimeException em resposta 500")
    void shouldHandleUnexpectedException() {
        var response = handler.handleRuntimeException(new RuntimeException("unexpected"));
        var body = response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, body.httpStatusCode());
        assertEquals("internal_server_error", body.errorCode());
        assertEquals("unexpected", body.message());
        assertNull(body.fields());
    }
}
