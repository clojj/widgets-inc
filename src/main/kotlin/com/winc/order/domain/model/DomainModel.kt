package com.winc.order.domain.model

import com.winc.order.domain.model.value.WidgetCode
import org.jmolecules.ddd.annotation.Entity

@Entity
data class Order(val code: WidgetCode, val amount: Int)
