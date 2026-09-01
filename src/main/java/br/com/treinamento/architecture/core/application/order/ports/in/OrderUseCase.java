package br.com.treinamento.architecture.core.application.order.ports.in;

import java.util.UUID;

import br.com.treinamento.architecture.core.domain.order.Orders;

public interface OrderUseCase {
    Orders createOrder(Orders order);
    Orders getOrderById(UUID orderId);
}
