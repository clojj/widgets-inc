package com.winc.product.adapter.outbound.persistence.r2dbc

import arrow.core.Either.Companion.catch
import arrow.core.left
import arrow.core.nonEmptyListOf
import com.winc.product.application.port.inbound.Transact
import com.winc.product.application.port.outbound.SaveProduct
import com.winc.product.application.port.outbound.UpdateProduct
import hexa.HEXA
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.util.*

@HEXA.AdapterOutbound
fun saveProductAdapter(productRepository: ProductRepository): SaveProduct = { product ->
    if (product.uuid == null) {
        catch({
            nonEmptyListOf("${it.message}")
        }) {
            productRepository.save(product.toEntity()).awaitFirst().uuid!!
        }
    } else {
        nonEmptyListOf("can't save a new product with existing id: $product").left()
    }
}

@HEXA.AdapterOutbound
fun updateProductAdapter(productRepository: ProductRepository): UpdateProduct = { product ->
    if (product.uuid != null) {
        catch({
            nonEmptyListOf("${it.message}")
        }) {
            productRepository.save(product.toEntity()).awaitFirst().toDomain()
        }
    } else {
        nonEmptyListOf("can't update a product without id: $product").left()
    }
}

@Repository
interface ProductRepository : ReactiveCrudRepository<ProductEntity, UUID>

fun <A> transactionAdapter(txOperator: TransactionalOperator): Transact<A> {
    return { block -> txOperator.executeAndAwait { tx -> block() } ?: TODO() }
}
