package com.winc.order.adapter.persistence.r2dbc

import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import com.winc.order.domain.model.Order
import com.winc.order.domain.port.`in`.SaveOrder
import com.winc.order.domain.port.out.OrderRepo
import ddd.HEXA
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@HEXA.Adapter
val createOrderAdapter: (OrderRepo<OrderEntity, UUID>) -> SaveOrder = { orderRepo: OrderRepo<OrderEntity, UUID> ->
    { order: Order ->
        val orderEntity = OrderEntity(code = order.code.code, amount = order.amount)
        val entity = orderRepo.save(orderEntity).awaitFirst()
        if (entity?.uuid != null) {
            entity.uuid!!.right()
        } else {
            nonEmptyListOf("error inserting").left()
        }
    }
}

@Repository
interface OrderRepository : ReactiveCrudRepository<OrderEntity, UUID>, OrderRepo<OrderEntity, UUID>
