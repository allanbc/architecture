package br.com.treinamento.architecture.core.domain.exception;

import java.util.Map;

public class QuantityNullException extends OrderManagementException {

    public QuantityNullException() {
        super("QUANTITY_NULL", "Quantity cannot be null", Map.of("quantity", "Quantity cannot be null"));
    }
    
}
