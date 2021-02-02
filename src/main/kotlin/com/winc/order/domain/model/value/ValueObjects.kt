package com.winc.order.domain.model.value

import arrow.core.left
import arrow.core.right
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import dev.forkhandles.values.regex

inline class WidgetCode private constructor(override val value: String) : Value<String> {
    // TODO there should be more validations than 1
    companion object : StringValueFactory<WidgetCode>(::WidgetCode, "^[A-Z]*\\d{3,5}".regex)
}

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.off(primitive: PRIMITIVE) =
    // TODO it's one exception too many... there should be a "value4arrow"
    try {
        this.of(primitive).right()
    } catch (e: Throwable) {
        e.message.left()
    }
