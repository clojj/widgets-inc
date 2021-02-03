package com.winc.order.domain.model.value

import arrow.core.left
import arrow.core.right
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.jsonschema.pattern
import org.jmolecules.ddd.annotation.ValueObject

@ValueObject // only type + companion are public
inline class WidgetCode private constructor(val code: String) {
    companion object {
        val validate = Validation<WidgetCode> {
            WidgetCode::code {
                pattern("^[A-Z]{1}\\d{3,5}")
            }
        }
        fun of(string: String) = validate(WidgetCode(string)).asEither()
    }
}

// arrow adapter
private fun <T> ValidationResult<T>.asEither() =
    if (this is Valid) this.value.right() else this.errors.left()
