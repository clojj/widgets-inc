package com.winc.order.domain.ports.incoming

import arrow.core.Either
import com.winc.order.domain.model.value.WidgetCode
import java.util.*

interface OrderApplication {
    suspend fun createOrder(createOrderCommand: CreateOrderCommand): Either<List<String>, OrderCreatedEvent>
    fun createCheckWidgetCodeUseCase(): (String) -> Either<List<String>, Pair<String, WidgetCode>>
}

data class CreateOrderCommand(val code: String, val amount: Int)

data class OrderCreatedEvent(val orderId: UUID)
