package br.com.treinamento.architecture.core.domain.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import br.com.treinamento.architecture.core.domain.exception.QuantityNullException;
import br.com.treinamento.architecture.core.domain.exception.QuantityInvalidException;
import br.com.treinamento.architecture.core.domain.exception.SkuEmptyException;
import br.com.treinamento.architecture.core.domain.exception.SkuNullException;
import br.com.treinamento.architecture.core.domain.exception.TotalPriceInvalidException;
import br.com.treinamento.architecture.core.domain.exception.TotalPriceNullException;
import br.com.treinamento.architecture.core.domain.exception.UnitPriceInvalidException;
import br.com.treinamento.architecture.core.domain.exception.UnitPriceNullException;
import br.com.treinamento.architecture.core.domain.validation.OrderValidation;
import lombok.Getter;

@Getter
public class OrderItem {

    private static final OrderValidation ORDER_VALIDATION = new OrderValidation();

    private UUID id;
    private UUID orderId;
    private String sku;
    private Integer quantity;
    private BigDecimal unitPrice = BigDecimal.ZERO;

    public OrderItem(UUID id, UUID orderId, String sku, Integer quantity, BigDecimal unitPrice) {

        ORDER_VALIDATION.validate(orderId, sku, quantity, unitPrice);

        this.id = id;
        this.orderId = orderId;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

	public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void updateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new QuantityNullException();
        }
        if(quantity <= 0) {
            throw new QuantityInvalidException();
        }
        this.quantity = quantity;
    }

    public void updateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null) {
            throw new UnitPriceNullException();
        }
        if(unitPrice.signum() <= 0) {
            throw new UnitPriceInvalidException();
        }
        this.unitPrice = unitPrice;
    }

    public void updateSku(String sku) {
        if (sku == null) {
            throw new SkuNullException();
        }
        if(sku.isBlank()) {
            throw new SkuEmptyException();
        }
        this.sku = sku;
    }

    public void updateTotalPrice(BigDecimal totalPrice) {
        if (totalPrice == null) {
            throw new TotalPriceNullException();
        }
        if(totalPrice.signum() < 0) {
            throw new TotalPriceInvalidException();
        }
        if(quantity <= 0) {
            throw new QuantityInvalidException();
        }

        this.unitPrice = totalPrice.divide(
            BigDecimal.valueOf(quantity), 
            2, 
            RoundingMode.HALF_UP);
    }
}
