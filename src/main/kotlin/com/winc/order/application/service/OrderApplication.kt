package com.winc.order.application.service

import arrow.core.Either
import arrow.core.Nel
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.right
import com.winc.order.domain.model.Order
import com.winc.order.domain.model.value.WidgetCode
import com.winc.order.domain.port.`in`.CreateOrderCommand
import com.winc.order.domain.port.`in`.OrderApplication
import com.winc.order.domain.port.`in`.OrderCreatedEvent
import com.winc.order.domain.service.someDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class OrderApplication : OrderApplication {

    override fun checkWidgetCode(): (String) -> Either<List<String>, Pair<String, WidgetCode>> {
        // TODO retrieve authorities from spring security context here ?
        return { code -> checkWidgetCodeUseCase(code) }
    }

    // TODO general sealed hierarchy for error type (ValidationErrors is just one choice-type)
    @Transactional
    override suspend fun createOrder(createOrderCommand: CreateOrderCommand): Either<Nel<String>, OrderCreatedEvent> =
        either {
            val validatedOrder = validateOrder(createOrderCommand).bind()
            val orderCreatedEvent = processOrder(validatedOrder).bind()
            orderCreatedEvent
        }

    suspend fun validateOrder(newOrder: CreateOrderCommand): Validated<Nel<String>, Order> =
        Order.of(newOrder.code, newOrder.amount)

    fun processOrder(validatedOrder: Order): Either<Nel<String>, OrderCreatedEvent> {
        val uuid = insertOrder(validatedOrder)
        return OrderCreatedEvent(uuid).right()
    }

    // TODO interface of outgoing persistence-adapter
    private fun insertOrder(validatedOrder: Order): UUID {
        return UUID.randomUUID()
    }

    private fun checkWidgetCodeUseCase(widgetcode: String): Either<List<String>, Pair<String, WidgetCode>> {
        // domain validates incoming
        val widgetCode = WidgetCode.of(widgetcode).toEither()
        return widgetCode.map { someDomainService(it) }
    }
}
