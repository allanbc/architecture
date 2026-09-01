package br.com.treinamento.architecture.core.domain.exception;

import java.util.Map;

public class SkuNullException extends OrderManagementException {

    public SkuNullException() {
        super("SKU_NULL", "Sku cannot be null", Map.of("sku", "Sku cannot be null"));
    }
}
