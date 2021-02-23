package com.winc.order.application.service

import arrow.core.Either
import arrow.core.Nel
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.right
import com.winc.order.application.port.`in`.OrderApplication
import com.winc.order.domain.model.Order
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.spi.ConnectionFactory
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.util.*

@Service
class OrderApplication : OrderApplication {

    // TODO general sealed hierarchy for error type (ValidationErrors is just one choice-type)
    @Transactional
    override suspend fun createOrder(createOrderCommand: com.winc.order.application.port.`in`.CreateOrderCommand): Either<Nel<String>, com.winc.order.application.port.`in`.OrderCreatedEvent> =
        either {
            val validatedOrder = validateOrder(createOrderCommand).bind()
            val orderCreatedEvent = processOrder(validatedOrder).bind()
            orderCreatedEvent
        }

    suspend fun validateOrder(newOrder: com.winc.order.application.port.`in`.CreateOrderCommand): Validated<Nel<String>, Order> =
        Order.of(newOrder.code, newOrder.amount)

    fun processOrder(validatedOrder: Order): Either<Nel<String>, com.winc.order.application.port.`in`.OrderCreatedEvent> {
        val uuid = insertOrder(validatedOrder)
        return com.winc.order.application.port.`in`.OrderCreatedEvent(uuid).right()
    }

    // TODO interface of outgoing persistence-adapter
    private fun insertOrder(validatedOrder: Order): UUID {
        return UUID.randomUUID()
    }

}

fun orderUseCase(connectionFactory: ConnectionPool, adapter: com.winc.order.application.port.`in`.SaveOrder) =
    object : com.winc.order.application.port.`in`.CreateOrderUseCase {
        override val createOrder = adapter

        override suspend fun com.winc.order.application.port.`in`.CreateOrderCommand.exec(): Either<Nel<String>, UUID> {
            return runWriteTx(connectionFactory) { reactiveTx ->
                either {
                    val validatedOrder = Order.of(code, amount).bind()
                    createOrder(validatedOrder).bind()
                }
            }
        }
    }

suspend fun <A> runWriteTx(connectionFactory: ConnectionFactory, block: suspend (ReactiveTransaction) -> A): A {
    val tm = R2dbcTransactionManager(connectionFactory)
    val txo = TransactionalOperator.create(tm)
    return txo.executeAndAwait(block) ?: TODO()
}

