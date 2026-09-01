package br.com.treinamento.architecture.infrastructure.adapters.out.persistence.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.treinamento.architecture.core.domain.enums.OrderStatusEnum;
import br.com.treinamento.architecture.core.domain.order.OrderItem;
import br.com.treinamento.architecture.core.domain.order.Orders;
import br.com.treinamento.architecture.infrastructure.adapters.out.database.OrdersItemsEntity;
import br.com.treinamento.architecture.infrastructure.adapters.out.database.OrderEntity;

@Mapper(componentModel = "spring")
public interface OrdersPersistenceMapper {
    
   
    @Mapping(target = "id", ignore = true)
    OrderEntity toEntity(Orders order);
   
    Orders toDomain(OrderEntity entity);

    @Mapping(target = "order", ignore = true)
    OrdersItemsEntity toEntity(OrderItem item);

    OrderItem toDomain(OrdersItemsEntity entity);

    @AfterMapping
    default void linkItemsToOrder(@MappingTarget OrderEntity entity) {
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setOrder(entity));
        }
    }

    default String map(OrderStatusEnum status) {
        return status == null ? null : status.name();
    }

    default OrderStatusEnum map(String status) {
        return status == null ? null : OrderStatusEnum.valueOf(status);
    }

    default Orders toDomain(OrderEntity entity, Orders order) {
        if (entity == null) {
            return order;
        }
        return toDomain(entity);
    }

    default OrderEntity toEntity(Orders order, OrderEntity entity) {
        if (order == null) {
            return entity;
        }
        return toEntity(order);
    }

    default List<Orders> toDomainList(List<OrderEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

}
