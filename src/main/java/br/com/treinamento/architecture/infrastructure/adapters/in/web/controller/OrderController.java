package br.com.treinamento.architecture.infrastructure.adapters.in.web.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.treinamento.architecture.core.application.order.ports.in.OrderUseCase;
import br.com.treinamento.architecture.core.domain.order.Orders;
import br.com.treinamento.architecture.infrastructure.adapters.in.web.dto.RequestOrder;
import br.com.treinamento.architecture.infrastructure.adapters.in.web.mapper.OrderMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {
    
    private static final Logger logger = getLogger(OrderController.class);
    private final OrderUseCase orderUseCase;
    private final OrderMapper orderMapper;

    public OrderController(OrderUseCase orderUseCase, OrderMapper orderMapper) {
        this.orderUseCase = orderUseCase;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<Orders> createOrder(@RequestBody @Valid RequestOrder orderRequest) {
        logger.info("Creating order: {}", orderRequest);
        var createdOrder = orderUseCase.createOrder(orderMapper.toDomain(orderRequest));
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orders> getOrderById(@PathVariable("id") UUID id) {
        // Implementation for retrieving an order by ID
        return ResponseEntity.ok(orderUseCase.getOrderById(id));
    }

}
