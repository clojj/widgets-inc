package com.winc.order.adapter.cli

import com.winc.order.application.useCase

fun main() {
    val result = useCase("A123")
    println(result)
}
