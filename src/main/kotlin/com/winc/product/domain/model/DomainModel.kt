package com.winc.product.domain.model

import arrow.core.*
import arrow.core.Validated.Companion.invalidNel
import arrow.typeclasses.Semigroup
import ddd.DDD
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.jsonschema.pattern
import org.jmolecules.ddd.annotation.ValueObject

inline val String.value: String
    get() = this

// TODO as @Aggregate
@DDD.Entity
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

// TODO konform validations
@ValueObject // only type + companion are public
inline class WidgetCode private constructor(val code: String) {
    companion object {
        val validate = Validation<WidgetCode> {
            WidgetCode::code {
                pattern("^[A-Z]{1}\\d{3,5}")
            }
        }
        fun of(string: String): Validated<Nel<String>, WidgetCode> = validate(WidgetCode(string)).asValidated()
    }
}

// arrow adapter
private fun <T> ValidationResult<T>.asValidated() =
    if (this is Valid) this.value.valid() else Nel.fromListUnsafe(this.errors.map { "VALIDATION ERROR: ${it.message} in ${it.dataPath}" }).invalid()

