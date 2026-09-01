package br.com.treinamento.architecture.core.domain.exception;

import java.util.Map;

public class OrderIdNullException extends OrderManagementException {

    public OrderIdNullException() {
        super("ORDER_ID_NULL", "Order id cannot be null", Map.of("orderId", "Order id cannot be null"));
    }
}
