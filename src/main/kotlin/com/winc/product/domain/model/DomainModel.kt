package com.winc.product.domain.model

import arrow.core.Nel
import arrow.core.Validated
import arrow.core.Validated.Companion.invalidNel
import arrow.core.nonEmptyList
import arrow.core.valid
import arrow.typeclasses.Semigroup
import com.winc.product.domain.model.Error.ValidationError
import ddd.DDD
import hexa.HEXA
import java.util.*

@HEXA.Domain
@DDD.Entity // TODO as @Aggregate
data class Product(val code: ProductCode, val name: String, val uuid: UUID? = null) {

    companion object {
        private fun validateName(name: String) =
            if (name.isEmpty())
                invalidNel("product name must not be empty")
            else
                name.valid()

        fun of(code: String, name: String): Validated<Error, Product> {
            val productCode: Validated<Nel<String>, ProductCode> = ProductCode.of(code)
            val validatedName: Validated<Nel<String>, String> = validateName(name)
            return Validated.mapN(Semigroup.nonEmptyList(), productCode, validatedName) { code_, name_ ->
                Product(code_, name_)
            }.mapLeft { ValidationError(it) }
        }
    }
}

sealed class Error {
    data class InfraError(val error: String) : Error()
    data class ValidationError(val validationErrors: Nel<String>) : Error()
    data class PortOutboundError(val message: String) : Error()
}
