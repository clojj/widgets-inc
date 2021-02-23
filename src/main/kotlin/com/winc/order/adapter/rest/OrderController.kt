package com.winc.order.adapter.rest

import com.winc.order.application.port.`in`.OrderApplication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(
    private val orderApplication: OrderApplication
) {

    @PostMapping("/orders", consumes = ["application/json"], produces = ["application/json"])
    suspend fun createOrder(@RequestBody newOrder: NewOrder): ResponseEntity<OrderReceipt> =
        orderApplication.createOrder(newOrder.toCreateOrderCommand())
            .fold({
                throw PayloadException(ErrorResponse("$it via global handler"))
            }) {
                ResponseEntity.ok(OrderReceipt(it.orderId))
            }

}
