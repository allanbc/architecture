package br.com.treinamento.architecture.core.domain.exception;

import java.util.Map;

public class UnitPriceNullException extends OrderManagementException {

    public UnitPriceNullException() {
        super("UNIT_PRICE_NULL", "Unit price cannot be null", Map.of("unitPrice", "Unit price cannot be null"));
    }
}
