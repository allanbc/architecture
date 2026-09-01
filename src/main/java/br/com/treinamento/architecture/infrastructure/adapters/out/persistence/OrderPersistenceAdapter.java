package br.com.treinamento.architecture.infrastructure.adapters.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.treinamento.architecture.core.application.order.ports.out.OrderRepository;
import br.com.treinamento.architecture.core.domain.order.Orders;
import br.com.treinamento.architecture.infrastructure.adapters.out.persistence.mapper.OrdersPersistenceMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrdersPersistenceMapper orderPersistenceMapper;

    @Override
    public Orders saveOrder(Orders order) {
        return orderPersistenceMapper
                    .toDomain(orderJpaRepository.save(orderPersistenceMapper.toEntity(order)));
    }

    @Override
    public Orders getOrderById(UUID orderId) {
        return orderPersistenceMapper.toDomain(orderJpaRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found")));
    }
    
}
