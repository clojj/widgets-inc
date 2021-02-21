package com.winc.order.adapter.rest

import com.winc.order.adapter.persistence.r2dbc.OrderRepository
import com.winc.order.adapter.persistence.r2dbc.createOrderAdapter
import com.winc.order.application.service.CreateOrderUseCase
import com.winc.order.application.service.runWriteTx
import io.r2dbc.pool.ConnectionPool
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class OrderControllerFun(private val orderRepository: OrderRepository, val connectionFactory: ConnectionPool) {

    @PostMapping("/fun/orders", consumes = ["application/json"], produces = ["application/json"])
    suspend fun createOrderFun(@RequestBody newOrder: NewOrder): ResponseEntity<UUID> =
        object : CreateOrderUseCase {
            override val createOrder = createOrderAdapter(orderRepository)
        }.run {
            // runs in scope of CreateOrderUseCase
            runWriteTx(connectionFactory) { reactiveTx ->
                val result = newOrder.toCommand().runUseCase()

                // TODO test reactiveTx
                // reactiveTx.setRollbackOnly()

                result.fold({ TODO("error json-response only via ExceptionHandler ?") }) { ResponseEntity.ok(it) }
            }
        }
}
