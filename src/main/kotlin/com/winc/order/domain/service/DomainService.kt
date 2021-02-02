package com.winc.order.domain.service

import com.winc.order.application.useCase
import com.winc.order.domain.model.WidgetCode

fun domainService(widgetCode: WidgetCode): Int {
    return widgetCode.value.toInt()
}
