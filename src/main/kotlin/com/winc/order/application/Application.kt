package com.winc.order.application

import arrow.core.Either
import com.winc.order.domain.model.value.WidgetCode
import com.winc.order.domain.service.domainService
import io.konform.validation.ValidationErrors

fun useCase(widgetcode: String): Either<ValidationErrors, Pair<String, WidgetCode>> {

    // domain validates incoming
    val widgetCode = WidgetCode.of(widgetcode)

    return widgetCode.map { domainService(it) }
}
