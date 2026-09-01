package br.com.treinamento.architecture.core.domain.exception;

import java.util.Map;

public class UnitPriceInvalidException extends OrderManagementException {

    public UnitPriceInvalidException() {
        super("UNIT_PRICE_INVALID", "Unit price must be greater than zero", Map.of("unitPrice", "Unit price must be greater than zero"));
    }
}
