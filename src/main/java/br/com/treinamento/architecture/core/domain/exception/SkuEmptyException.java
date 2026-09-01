package br.com.treinamento.architecture.core.domain.exception;

import java.util.Map;

public class SkuEmptyException extends OrderManagementException {

    public SkuEmptyException() {
        super("SKU_EMPTY", "Sku cannot be empty", Map.of("sku", "Sku cannot be empty"));
    }
}
