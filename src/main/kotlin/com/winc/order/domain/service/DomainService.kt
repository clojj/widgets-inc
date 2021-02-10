package com.winc.order.domain.service

import com.winc.order.domain.model.value.WidgetCode
import ddd.Service

@Service
fun someDomainService(widgetCode: WidgetCode): Pair<String, WidgetCode> {
    return "approved" to widgetCode
}
