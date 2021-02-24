package com.winc.product.config

import arrow.core.Either
import arrow.core.Nel
import com.winc.product.adapter.out.persistence.r2dbc.ProductRepository
import com.winc.product.adapter.out.persistence.r2dbc.saveProductAdapter
import com.winc.product.adapter.out.persistence.r2dbc.writeTransactionAdapter
import com.winc.product.application.port.`in`.Transact
import com.winc.product.application.port.out.SaveProduct
import com.winc.product.application.service.CreateProductUseCase
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import java.util.*

@Component
class CreateProduct(productRepository: ProductRepository, transactionalOperator: TransactionalOperator) :
    CreateProductUseCase {
    override val transact: Transact<Either<Nel<String>, UUID>> = writeTransactionAdapter(transactionalOperator)
    override val saveProduct: SaveProduct = saveProductAdapter(productRepository)
}
