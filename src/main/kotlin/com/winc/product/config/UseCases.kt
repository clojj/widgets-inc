package com.winc.product.config

import arrow.core.Either
import arrow.core.Nel
import com.winc.product.adapter.persistence.r2dbc.ProductRepository
import com.winc.product.adapter.persistence.r2dbc.saveProductAdapter
import com.winc.product.adapter.persistence.tx.writeTransaction
import com.winc.product.application.CreateProductCommand
import com.winc.product.application.CreateProductUseCase
import com.winc.product.application.port.`in`.Transact
import com.winc.product.application.port.out.SaveProduct
import io.r2dbc.spi.ConnectionFactory
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestController
import java.util.*

@Component
class CreateProduct(private val connectionFactory: ConnectionFactory, private val productRepository: ProductRepository) : CreateProductUseCase {
    override val transact: Transact<Either<Nel<String>, UUID>> = writeTransaction(connectionFactory)
    override val saveProduct: SaveProduct = saveProductAdapter(productRepository)
}

// move to rest adapter
@RestController
class ProductController(val createProduct: CreateProduct) {

    suspend fun test() {
        createProduct.run {
            CreateProductCommand("code", "name").exec()
        }
    }
}
