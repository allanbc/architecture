package br.com.treinamento.architecture.infrastructure.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.treinamento.architecture.infrastructure.adapters.out.database.OrderEntity;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}
