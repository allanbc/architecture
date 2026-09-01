package br.com.treinamento.architecture.core.domain.exception;

import java.util.Map;

public class TotalPriceInvalidException extends OrderManagementException {

    public TotalPriceInvalidException() {
        super("TOTAL_PRICE_INVALID", "Total price cannot be negative", Map.of("totalPrice", "Total price cannot be negative"));
    }
}
