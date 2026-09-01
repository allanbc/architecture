package br.com.treinamento.architecture.core.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.ParameterDeclarations;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Exceções de domínio - testes unitários")
class OrderManagementExceptionTest {

    @ParameterizedTest(name = "{index} => {1}")
    @ArgumentsSource(DomainExceptionArgumentsProvider.class)
    @Order(1)
    @DisplayName("deve expor código, descrição e campo padronizados")
    void shouldExposeStandardMetadata(OrderManagementException exception, String code,
            String message, String field) {
        assertEquals(code, exception.getErrorCode());
        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getErrorDescription());
        assertEquals(Map.of(field, message), exception.getFields());
    }

    @Test
    @Order(2)
    @DisplayName("deve representar código e campos no toString")
    void shouldHaveDiagnosticString() {
        var exception = new QuantityInvalidException();
        assertTrue(exception.toString().contains("QUANTITY_INVALID"));
        assertTrue(exception.toString().contains("quantity"));
    }

    static class DomainExceptionArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(
                ParameterDeclarations parameters, ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new OrderIdNullException(), "ORDER_ID_NULL", "Order id cannot be null", "orderId"),
                    Arguments.of(new QuantityInvalidException(), "QUANTITY_INVALID", "Quantity must be greater than zero", "quantity"),
                    Arguments.of(new QuantityNullException(), "QUANTITY_NULL", "Quantity cannot be null", "quantity"),
                    Arguments.of(new SkuEmptyException(), "SKU_EMPTY", "Sku cannot be empty", "sku"),
                    Arguments.of(new SkuNullException(), "SKU_NULL", "Sku cannot be null", "sku"),
                    Arguments.of(new TotalPriceInvalidException(), "TOTAL_PRICE_INVALID", "Total price cannot be negative", "totalPrice"),
                    Arguments.of(new TotalPriceNullException(), "TOTAL_PRICE_NULL", "Total price cannot be null", "totalPrice"),
                    Arguments.of(new UnitPriceInvalidException(), "UNIT_PRICE_INVALID", "Unit price must be greater than zero", "unitPrice"),
                    Arguments.of(new UnitPriceNullException(), "UNIT_PRICE_NULL", "Unit price cannot be null", "unitPrice"));
        }
    }
}
