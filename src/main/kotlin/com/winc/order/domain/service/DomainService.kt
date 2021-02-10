package com.winc.order.domain.service

import com.winc.order.domain.model.value.WidgetCode
import ddd.DDD

@DDD.DomainService
fun someDomainService(widgetCode: WidgetCode): Pair<String, WidgetCode> {
    return "approved" to widgetCode
}
