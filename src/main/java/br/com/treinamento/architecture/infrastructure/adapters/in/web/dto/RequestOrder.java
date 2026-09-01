package br.com.treinamento.architecture.infrastructure.adapters.in.web.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RequestOrder(
        
        @NotNull(message = "Customer ID is required")
        UUID customerId,
        
        @Valid
        @NotEmpty(message = "Order must contain at least one item")
        List<RequestOrderItem> items) {

        public RequestOrder(
                
                UUID customerId,
                List<RequestOrderItem> items
        ) 
        {
            this.customerId = customerId;
            this.items = items;
        }
}
