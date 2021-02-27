package com.winc.product.config

import arrow.core.Either
import com.winc.product.adapter.outbound.persistence.r2dbc.ProductRepository
import com.winc.product.adapter.outbound.persistence.r2dbc.saveProductAdapter
import com.winc.product.adapter.outbound.persistence.r2dbc.transactionAdapter
import com.winc.product.application.port.inbound.Transact
import com.winc.product.application.port.outbound.SaveProduct
import com.winc.product.application.service.CreateProductUseCase
import com.winc.product.domain.model.Error
import com.winc.product.domain.model.Product.ValidProduct
import ddd.DDD
import hexa.HEXA
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator

@DDD.UseCase
@HEXA.Config
@Component
class CreateProduct(productRepository: ProductRepository, txWriteOperator: TransactionalOperator) : CreateProductUseCase {
    override val transact: Transact<Either<Error, ValidProduct>> = transactionAdapter(txWriteOperator)
    override val saveProduct: SaveProduct = saveProductAdapter(productRepository)
}
