package com.winc.order.adapter.cli

import com.winc.order.application.service.OrderApplication

fun main() {
    val result = OrderApplication().checkWidgetCode()("A123")
    println(result)
}
