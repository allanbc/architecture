package br.com.treinamento.architecture.core.domain.exception;

import java.util.Map;

public class QuantityInvalidException extends OrderManagementException {

    public QuantityInvalidException() {
        super("QUANTITY_INVALID", "Quantity must be greater than zero", Map.of("quantity", "Quantity must be greater than zero"));
    }
}
