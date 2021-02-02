package com.winc.order.application

import com.winc.order.domain.model.WidgetCode
import com.winc.order.domain.service.domainService

fun useCase(): Int {
    val widgetCode = WidgetCode.of("1234")
    return domainService(widgetCode)
}
