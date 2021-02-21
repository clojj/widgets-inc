package com.winc.order.domain.port.`in`

import arrow.core.Either
import arrow.core.Nel
import com.winc.order.domain.model.Order
import com.winc.order.domain.model.value.WidgetCode
import ddd.DDD
import java.util.*

interface OrderApplication {

    @DDD.UseCase
    suspend fun createOrder(createOrderCommand: CreateOrderCommand): Either<List<String>, OrderCreatedEvent>

    @DDD.UseCase
    fun checkWidgetCode(): (String) -> Either<List<String>, Pair<String, WidgetCode>>
}


typealias SaveOrder = suspend (order: Order) -> Either<Nel<String>, UUID>

@DDD.UseCase
interface CreateOrderUseCase {
    val createOrder: SaveOrder

    suspend fun CreateOrderCommand.exec(): Either<Nel<String>, UUID>
}

data class CreateOrderCommand(val code: String, val amount: Int)

data class OrderCreatedEvent(val orderId: UUID)
