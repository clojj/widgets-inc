package com.winc.product.application.service

import arrow.core.Either
import arrow.core.Nel
import arrow.core.computations.either
import com.winc.product.application.port.inbound.Transact
import com.winc.product.application.port.outbound.SaveProduct
import com.winc.product.domain.model.Product
import ddd.DDD
import hexa.HEXA
import java.util.*

@DDD.ApplicationService
@HEXA.Application
interface CreateProductUseCase {
    val transact: Transact<Either<Nel<String>, UUID>>
    val saveProduct: SaveProduct

    // TODO general sealed hierarchy for error type (ValidationErrors is just one choice-type)
    suspend fun CreateProductCommand.execute(): Either<Nel<String>, UUID> =
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
