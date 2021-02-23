package com.winc.product.adapter.out.persistence.tx

import com.winc.product.application.port.`in`.Transact
import io.r2dbc.spi.ConnectionFactory
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

fun <A> writeTransaction(connectionFactory: ConnectionFactory): Transact<A> {
    val transactionManager = R2dbcTransactionManager(connectionFactory)
    val transactionalOperator = TransactionalOperator.create(transactionManager)
    return { block -> transactionalOperator.executeAndAwait { tx -> block() } ?: TODO() }
}

