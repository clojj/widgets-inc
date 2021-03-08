package com.winc.product.adapter.outbound.persistence.datajdbc

import arrow.core.Either
import com.winc.product.adapter.outbound.persistence.data.ProductEntity
import com.winc.product.adapter.outbound.persistence.data.toDomain
import com.winc.product.adapter.outbound.persistence.data.toEntity
import com.winc.product.application.port.inbound.Transact
import com.winc.product.application.port.outbound.SaveProduct
import com.winc.product.domain.model.Error
import hexa.HEXA
import kotlinx.coroutines.runBlocking
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.*

@HEXA.AdapterOutbound
fun saveProductAdapter(productRepository: ProductRepository): SaveProduct = { newProduct ->
    Either.catch({
        Error.PortOutboundError("${it.message}")
    }) {
        productRepository.save(newProduct.toEntity()).toDomain()
    }
}

@Repository
interface ProductRepository : CrudRepository<ProductEntity, UUID>

fun <A> transactionAdapter(txManager: PlatformTransactionManager): Transact<A> {
    val txTemplate = TransactionTemplate(txManager)
    return { block -> txTemplate.execute { tx -> runBlocking { block() } } ?: TODO() }
}
