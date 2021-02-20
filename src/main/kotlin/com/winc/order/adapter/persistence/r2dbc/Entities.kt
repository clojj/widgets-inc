package com.winc.order.adapter.persistence.r2dbc

import org.springframework.data.annotation.Id
import java.util.*

data class OrderEntity(@Id var uuid: UUID? = null, val code: String, val amount: Int)
