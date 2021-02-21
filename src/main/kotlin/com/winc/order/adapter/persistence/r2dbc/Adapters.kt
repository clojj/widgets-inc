package com.winc.order.adapter.persistence.r2dbc

import arrow.core.*
import com.winc.order.domain.model.Order
import ddd.HEXA
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@HEXA.Adapter
val createOrderAdapter: (OrderRepository) -> (suspend (Order) -> Either<Nel<String>, UUID>) = { orderRepository: OrderRepository ->
    { order: Order ->
        val orderEntity = OrderEntity(code = order.code.code, amount = order.amount)
        val entity = orderRepository.save(orderEntity).awaitFirst()
        if (entity?.uuid != null) {
            entity.uuid!!.right()
        } else {
            nonEmptyListOf("error inserting").left()
        }
    }
}

@Repository
interface OrderRepository : ReactiveCrudRepository<OrderEntity, UUID>
