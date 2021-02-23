package com.winc.product.domain.model

import arrow.core.Nel
import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.jsonschema.pattern
import org.jmolecules.ddd.annotation.ValueObject

@ValueObject // only type + companion are public
inline class ProductCode private constructor(val value: String) {
    companion object {
        val validate = Validation<ProductCode> {
            ProductCode::value {
                pattern("^[A-Z]{1}\\d{3,5}")
            }
        }
        fun of(string: String): Validated<Nel<String>, ProductCode> = validate(ProductCode(string)).asValidated()
    }
}

// arrow adapter
private fun <T> ValidationResult<T>.asValidated() =
    if (this is Valid) this.value.valid() else Nel.fromListUnsafe(this.errors.map { "VALIDATION ERROR: ${it.message} in ${it.dataPath}" }).invalid()
