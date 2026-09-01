package br.com.treinamento.architecture.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.treinamento.architecture.core.application.order.ports.in.OrderUseCase;
import br.com.treinamento.architecture.core.application.order.ports.out.OrderRepository;
import br.com.treinamento.architecture.core.application.service.OrderService;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderUseCase orderUseCase(OrderRepository orderRepository) {
        return new OrderService(orderRepository);
    }
    
}
