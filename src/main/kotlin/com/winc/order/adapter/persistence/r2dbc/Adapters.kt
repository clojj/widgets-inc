package com.winc.order.adapter.persistence.r2dbc

import arrow.core.Either
import arrow.core.Nel
import arrow.core.right
import com.winc.order.domain.model.Order
import ddd.HEXA
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@HEXA.Adapter
val createOrderAdapter: (OrderRepository) -> (Order) -> Either<Nel<String>, Mono<UUID>> = { orderRepository: OrderRepository ->
    { order: Order ->
        val orderEntity = OrderEntity(code = order.code.code, amount = order.amount)
        // TODO
        orderRepository.save(orderEntity).map { it.uuid!! }.right()
    }
}

@Repository
interface OrderRepository : ReactiveCrudRepository<OrderEntity, UUID>
