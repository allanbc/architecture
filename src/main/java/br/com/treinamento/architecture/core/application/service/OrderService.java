package br.com.treinamento.architecture.core.application.service;

import java.util.Objects;
import java.util.UUID;

import br.com.treinamento.architecture.core.application.order.ports.in.OrderUseCase;
import br.com.treinamento.architecture.core.application.order.ports.out.OrderRepository;
import br.com.treinamento.architecture.core.domain.order.Orders;

public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository);
    }

    @Override
    public Orders createOrder(Orders order) {
        Objects.requireNonNull(order);
        return orderRepository.saveOrder(order); 
    }

    @Override
    public Orders getOrderById(UUID orderId) {
        // Implementation for retrieving an order by ID
        Objects.requireNonNull(orderId);
        return orderRepository.getOrderById(orderId);
    }

}