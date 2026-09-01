package br.com.treinamento.architecture.core.domain.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.treinamento.architecture.core.domain.enums.OrderStatusEnum;
import lombok.Getter;

@Getter
public class Orders {
    private UUID id;
    private UUID custumerId;
    private OrderStatusEnum status;
    private BigDecimal total;
    private Instant createdAt;
    private List<OrderItem> items;
    
    public Orders(UUID id, UUID custumerId, OrderStatusEnum status, BigDecimal total, Instant createdAt, List<OrderItem> items) {
        
        Objects.requireNonNull(custumerId, "custumerId não pode ser nulo");
        Objects.requireNonNull(status, "status não pode ser nulo");
        Objects.requireNonNull(total, "total não pode ser nulo");
        if(total.signum() < 0) {
            throw new IllegalArgumentException("total não pode ser negativo");
        }
        Objects.requireNonNull(createdAt, "createdAt não pode ser nulo");
        Objects.requireNonNull(items, "items não pode ser nulo");

        this.id = id;
        this.custumerId = custumerId;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
        this.items = new ArrayList<>(items);
    }

    public void addItem(OrderItem item) {
        Objects.requireNonNull(item, "item não pode ser nulo");
        this.items.add(item);
    }

    public void removeItem(OrderItem item) {
        Objects.requireNonNull(item, "item não pode ser nulo");
        this.items.remove(item);
    }

    public void updateStatus(OrderStatusEnum status) {
        Objects.requireNonNull(status, "status não pode ser nulo");
        this.status = status;
    }

    public void updateTotal(BigDecimal total) {
        Objects.requireNonNull(total, "total não pode ser nulo");
        if(total.signum() < 0) {
            throw new IllegalArgumentException("total não pode ser negativo");
        }
        this.total = total;
    }

    public void cancel() {
        this.status = OrderStatusEnum.CANCELLED;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
