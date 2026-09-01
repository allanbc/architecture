package br.com.treinamento.architecture.core.domain.validation;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.treinamento.architecture.core.domain.exception.OrderIdNullException;
import br.com.treinamento.architecture.core.domain.exception.QuantityInvalidException;
import br.com.treinamento.architecture.core.domain.exception.QuantityNullException;
import br.com.treinamento.architecture.core.domain.exception.SkuEmptyException;
import br.com.treinamento.architecture.core.domain.exception.SkuNullException;
import br.com.treinamento.architecture.core.domain.exception.UnitPriceInvalidException;
import br.com.treinamento.architecture.core.domain.exception.UnitPriceNullException;

public class OrderValidation {

    public void validate(UUID orderId, String sku, Integer quantity, BigDecimal unitPrice) {
        validateOrderId(orderId);
        validateSku(sku);
        validateQuantity(quantity);
        validateUnitPrice(unitPrice);
    }
    
    public void validateUnitPrice(BigDecimal unitPrice) {
        
		if (unitPrice == null) {
            throw new UnitPriceNullException();
        }
        if(unitPrice.signum() <= 0) {
            throw new UnitPriceInvalidException();
        }
	}
	private void validateQuantity(Integer quantity) {
		if (quantity == null) {
            throw new QuantityNullException();
        }
        if (quantity <= 0) {
            throw new QuantityInvalidException();
        }
	}
	private void validateSku(String sku) {
		if (sku == null) {
            throw new SkuNullException();
        }
        if (sku.isBlank()) {
            throw new SkuEmptyException();
        }
	}
	private void validateOrderId(UUID orderId) {
        if (orderId == null) {
            throw new OrderIdNullException();
        }
	}
}
