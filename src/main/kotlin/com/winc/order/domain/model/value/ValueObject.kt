package com.winc.order.domain.model.value

import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.ValidationErrors
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
        fun of(string: String): Validated<ValidationErrors, WidgetCode> = validate(WidgetCode(string)).asValidated()
    }
}

// arrow adapter
private fun <T> ValidationResult<T>.asValidated() =
    if (this is Valid) this.value.valid() else this.errors.invalid()
