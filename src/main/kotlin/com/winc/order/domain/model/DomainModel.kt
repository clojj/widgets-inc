package com.winc.order.domain.model

import com.winc.order.domain.model.value.WidgetCode
import org.jmolecules.ddd.annotation.Entity

@Entity
class WidgetOrder(val widgetCode: WidgetCode, val description: String)
