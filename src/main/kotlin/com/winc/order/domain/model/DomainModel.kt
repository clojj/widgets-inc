package com.winc.order.domain.model

import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.Value
import dev.forkhandles.values.regex

inline class WidgetCode private constructor(override val value: String) : Value<String> {
    companion object : StringValueFactory<WidgetCode>(::WidgetCode, "\\d{4}".regex)
}
