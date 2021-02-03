package com.winc.order.adapter.cli

import com.winc.order.application.checkWidgetCodeUseCase

fun main() {
    val result = checkWidgetCodeUseCase("A123")
    println(result)
}
