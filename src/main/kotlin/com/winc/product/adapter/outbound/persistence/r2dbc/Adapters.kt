package com.winc.product.adapter.outbound.persistence.r2dbc

import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import com.winc.product.application.port.inbound.Transact
import com.winc.product.application.port.outbound.SaveProduct
import ddd.HEXA
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.util.*

@HEXA.Adapter
fun saveProductAdapter(productRepository: ProductRepository) : SaveProduct = { product ->
        val productEntity = ProductEntity(code = product.code.value, name = product.name)
        val entity = productRepository.save(productEntity).awaitFirst()
        if (entity?.uuid != null) {
            entity.uuid!!.right()
        } else {
            nonEmptyListOf("error inserting").left()
        }
    }

@Repository
interface ProductRepository : ReactiveCrudRepository<ProductEntity, UUID>

fun <A> transactionAdapter(txOperator: TransactionalOperator): Transact<A> {
    return { block -> txOperator.executeAndAwait { tx -> block() } ?: TODO() }
}
