package com.winc.order.adapter.rest

import com.winc.order.domain.port.`in`.CreateOrderCommand
import java.util.*

data class NewOrder(val code: String, val amount: Int = 1)

fun NewOrder.toCreateOrderCommand(): CreateOrderCommand =
    CreateOrderCommand(code, amount)

data class OrderReceipt(val orderId: UUID)

data class ErrorResponse(val error: String)

data class PayloadException(val errorResponse: ErrorResponse) : Throwable()
