package com.winc.order.domain.service

import com.winc.order.domain.model.value.WidgetCode

fun domainService(widgetCode: WidgetCode): Pair<String, WidgetCode> {
    return "approved" to widgetCode
}
