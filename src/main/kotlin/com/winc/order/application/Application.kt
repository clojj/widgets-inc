package com.winc.order.application

import arrow.core.Either
import com.winc.order.domain.model.value.WidgetCode
import com.winc.order.domain.model.value.off
import com.winc.order.domain.service.domainService

fun useCase(widgetcode: String): Either<String?, Pair<String, WidgetCode>> {
    val widgetCode = WidgetCode.off(widgetcode)
    return widgetCode.map { domainService(it) }
}
