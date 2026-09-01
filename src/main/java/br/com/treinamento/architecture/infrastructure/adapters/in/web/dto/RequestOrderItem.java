package br.com.treinamento.architecture.infrastructure.adapters.in.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestOrderItem(
        @NotBlank(message = "SKU is required")
        String sku,
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        Integer quantity,
        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be greater than zero")
        @DecimalMin(value = "0.01", message = "Unit price must be greater than zero")
        BigDecimal unitPrice) {

        public RequestOrderItem(
                String sku,
                Integer quantity,
                BigDecimal unitPrice) {
            this.sku = sku;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
}
