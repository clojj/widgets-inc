package com.winc.order.adapter.rest

import com.winc.order.adapter.persistence.r2dbc.OrderEntity
import com.winc.order.adapter.persistence.r2dbc.createOrderAdapter
import com.winc.order.application.service.orderUseCase
import com.winc.order.domain.port.out.OrderRepo
import io.r2dbc.pool.ConnectionPool
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class OrderControllerFun(private val orderRepo: OrderRepo<OrderEntity, UUID>, val connectionFactory: ConnectionPool) {

    @PostMapping("/fun/orders", consumes = ["application/json"], produces = ["application/json"])
    suspend fun createOrderFun(@RequestBody newOrder: NewOrder): ResponseEntity<UUID> =

        orderUseCase(connectionFactory, createOrderAdapter(orderRepo)).run {
            // runs in scope of CreateOrderUseCase
            val result = newOrder.toCreateOrderCommand().exec()
            result.fold({ TODO("error json-response only via ExceptionHandler ?") }) { ResponseEntity.ok(it) }
        }
}

