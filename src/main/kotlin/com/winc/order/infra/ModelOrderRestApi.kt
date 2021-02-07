package com.winc.order.infra

import java.util.*

data class NewOrder(val code: String, val amount: Int = 1)

data class OrderReceipt(val orderId: UUID, val code: String, val amount: Int)

data class ErrorResponse(val error: String)

data class PayloadException(val errorResponse: ErrorResponse) : Throwable()
