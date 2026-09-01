package br.com.treinamento.architecture.core.domain.exception;

import java.util.Map;

public class TotalPriceNullException extends OrderManagementException {

    public TotalPriceNullException() {
        super("TOTAL_PRICE_NULL", "Total price cannot be null", Map.of("totalPrice", "Total price cannot be null"));
    }
}
