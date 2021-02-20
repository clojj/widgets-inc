package com.winc.order.application.service

import arrow.core.Either
import arrow.core.Nel
import arrow.core.computations.either
import com.winc.order.domain.model.Order
import com.winc.order.domain.port.`in`.CreateOrderCommand
import ddd.DDD
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import reactor.core.publisher.Mono
import java.util.*


typealias SaveOrder = (order: Order) -> Either<Nel<String>, Mono<UUID>>

@DDD.UseCase
interface CreateOrderUseCase {
    val createOrder: SaveOrder

    suspend fun CreateOrderCommand.runUseCase(): Either<Nel<String>, UUID> {
        return either {
            val validatedOrder = Order.of(code, amount).bind()
            val uuid = createOrder(validatedOrder).bind()
            uuid.awaitFirst()
        }
    }
}

suspend fun <A> runWriteTx(connectionFactory: ConnectionFactory, block: suspend (ReactiveTransaction) -> A): A {
    val tm = R2dbcTransactionManager(connectionFactory)
    val txo = TransactionalOperator.create(tm)
    val result = txo.executeAndAwait(block)
    return if (result != null) result else TODO()
}
