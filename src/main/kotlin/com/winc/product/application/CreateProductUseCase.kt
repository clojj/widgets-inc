package com.winc.product.application

import arrow.core.Either
import arrow.core.Nel
import arrow.core.computations.either
import com.winc.product.application.port.`in`.Transact
import com.winc.product.application.port.out.SaveProduct
import java.util.*


interface CreateProductUseCase {
    val transact: Transact<Either<Nel<String>, UUID>>
    val saveProduct: SaveProduct

    suspend fun CreateProductCommand.exec(): Either<Nel<String>, UUID> =
        transact {
            either {
                saveProduct(TODO()).bind()
            }
        }
}

data class CreateProductCommand(val code: String, val name: String)

data class ProductCreatedEvent(val productId: UUID)
