package com.winc.product.domain.model

import arrow.core.Nel
import arrow.core.Validated
import arrow.core.Validated.Companion.invalidNel
import arrow.core.valid
import arrow.core.zip
import com.winc.product.domain.model.Error.ValidationError
import ddd.DDD
import hexa.HEXA
import java.util.*

@HEXA.Domain
@DDD.Aggregate
sealed class Product {

    data class NewProduct(val code: ProductCode, val name: String) : Product()

    data class ValidProduct(val uuid: UUID, val code: ProductCode, val name: String) : Product()

    companion object {
        private fun validateName(name: String) =
            if (name.isEmpty())
                invalidNel("product name must not be empty")
            else
                name.valid()

        fun <T : Product> of(code: String, name: String, block: (ProductCode, String) -> T): Validated<Error, T> {
            val productCode: Validated<Nel<String>, ProductCode> = ProductCode.of(code)
            val validatedName: Validated<Nel<String>, String> = validateName(name)
            return productCode.zip(validatedName) { code_, name_ ->
                block(code_, name_)
            }.mapLeft { ValidationError(it) }
        }
    }

}

sealed class Error {
    data class InfraError(val error: String) : Error()
    data class ValidationError(val validationErrors: Nel<String>) : Error()
    data class PortOutboundError(val message: String) : Error()
}
