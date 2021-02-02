package com.winc.order.domain.model.value

import arrow.core.left
import arrow.core.right
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.jsonschema.pattern

inline class WidgetCode internal constructor(val code: String) : ValueObject {
    companion object {
        val validate = Validation<WidgetCode> {
            WidgetCode::code {
                pattern("^[A-Z]{1}\\d{3,5}")
            }
        }
        fun of(string: String) = validate(WidgetCode(string)).asEither()
    }
}

internal interface ValueObject // TODO "marker" alternatives ?

// arrow adapter ?
private fun <T> ValidationResult<T>.asEither() =
    if (this is Valid) this.value.right() else this.errors.left()
