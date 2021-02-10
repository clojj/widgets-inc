package com.winc.order.infra

import com.winc.order.application.OrderApplication
import java.util.*

data class NewOrder(val code: String, val amount: Int = 1)

fun NewOrder.toCommand(): OrderApplication.CreateOrderCommand =
    OrderApplication.CreateOrderCommand(code, amount)

data class OrderReceipt(val orderId: UUID)

data class ErrorResponse(val error: String)

data class PayloadException(val errorResponse: ErrorResponse) : Throwable()
