package com.winc.product.application.service

import arrow.core.Either
import arrow.core.computations.either
import com.winc.product.application.port.inbound.Transact
import com.winc.product.application.port.outbound.SaveProduct
import com.winc.product.domain.model.Error
import com.winc.product.domain.model.Product
import ddd.DDD
import hexa.HEXA
import java.util.*

@DDD.UseCase
@HEXA.Application
interface CreateProductUseCase {
    val transact: Transact<Either<Error, UUID>>
    val saveProduct: SaveProduct

    suspend fun CreateProductCommand.execute(): Either<Error, UUID> =
        transact {
            either {
                val product = Product.of(code, name).bind()
                saveProduct(product).bind()
            }
        }
}

@DDD.Command
data class CreateProductCommand(val code: String, val name: String)

// TODO return event
@DDD.Event
data class ProductCreatedEvent(val productId: UUID)
