package com.winc.product.domain.model

import arrow.core.Nel
import arrow.core.Validated
import arrow.core.Validated.Companion.invalidNel
import arrow.core.nonEmptyList
import arrow.core.valid
import arrow.typeclasses.Semigroup
import ddd.DDD
import hexa.HEXA

inline val String.value: String
    get() = this

@HEXA.Domain
@DDD.Entity // TODO as @Aggregate
data class Product(val code: ProductCode, val name: String) {

    companion object {
        fun validateName(name: String) =
            if (name.isEmpty())
                invalidNel("product name must not be empty")
            else
                name.valid()

        fun of(code: String, name: String): Validated<Nel<String>, Product> {
            val productCode: Validated<Nel<String>, ProductCode> = ProductCode.of(code)
            val validatedName: Validated<Nel<String>, String> = validateName(name)
            return Validated.mapN(Semigroup.nonEmptyList(), productCode, validatedName) { code_, name_ -> Product(code_, name_) }
        }
    }
}
