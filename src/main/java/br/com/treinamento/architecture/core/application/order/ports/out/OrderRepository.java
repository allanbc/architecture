package br.com.treinamento.architecture.core.application.order.ports.out;

import java.util.UUID;

import br.com.treinamento.architecture.core.domain.order.Orders;

public interface OrderRepository {
    Orders saveOrder(Orders order);
    Orders getOrderById(UUID orderId);
}
