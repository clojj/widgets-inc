package com.winc.order.application

import arrow.core.Either
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.right
import com.winc.order.domain.model.Order
import com.winc.order.domain.model.value.WidgetCode
import com.winc.order.domain.ports.incoming.CreateOrderCommand
import com.winc.order.domain.ports.incoming.OrderApplication
import com.winc.order.domain.ports.incoming.OrderCreatedEvent
import com.winc.order.domain.service.someDomainService
import ddd.UseCase
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderApplication : OrderApplication {

    @UseCase
    private fun checkWidgetCodeUseCase(widgetcode: String): Either<List<String>, Pair<String, WidgetCode>> {

        // domain validates incoming
        val widgetCode = WidgetCode.of(widgetcode).toEither()

        return widgetCode.map { someDomainService(it) }
    }

    override fun createCheckWidgetCodeUseCase(): (String) -> Either<List<String>, Pair<String, WidgetCode>> {
        // TODO retrieve authorities from spring security context here ?
        return { code -> checkWidgetCodeUseCase(code) }
    }

    // TODO general sealed hierarchy for error type (ValidationErrors is just one choice-type)

    // TODO onion violations:
    //  NewOrder -> NewOrderCommand
    //  move UUID to Order, return just UUID
    // TODO like a function of an @Aggregate ?
    //  Command -> State -> (State, [Event])
    @UseCase
    override suspend fun createOrder(createOrderCommand: CreateOrderCommand): Either<List<String>, OrderCreatedEvent> =
        either {
            val validatedOrder = validateOrder(createOrderCommand)()
            val orderCreatedEvent = processOrder(validatedOrder)()
            orderCreatedEvent
        }

    suspend fun validateOrder(newOrder: CreateOrderCommand): Validated<List<String>, Order> =
        Order.of(newOrder.code, newOrder.amount)

    fun processOrder(validatedOrder: Order): Either<List<String>, OrderCreatedEvent> {
        val uuid = insertOrder(validatedOrder)
        return OrderCreatedEvent(uuid).right()
    }

    // TODO interface of outgoing persistence-adapter
    private fun insertOrder(validatedOrder: Order): UUID {
        return UUID.randomUUID()
    }

}
