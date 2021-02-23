package com.winc.product.application.service

import arrow.core.Either
import arrow.core.Nel
import arrow.core.computations.either
import com.winc.product.application.port.`in`.Transact
import com.winc.product.application.port.out.SaveProduct
import com.winc.product.domain.model.Product
import java.util.*


interface CreateProductUseCase {
    val transact: Transact<Either<Nel<String>, UUID>>
    val saveProduct: SaveProduct

    suspend fun CreateProductCommand.exec(): Either<Nel<String>, UUID> =
        transact {
            either {
                val product = Product.of(code, name).bind()
                saveProduct(product).bind()
            }
        }
}

data class CreateProductCommand(val code: String, val name: String)

data class ProductCreatedEvent(val productId: UUID)
