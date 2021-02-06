package com.winc.order.application

import arrow.core.Either
import com.winc.order.domain.model.value.WidgetCode
import com.winc.order.domain.service.domainService
import ddd.UseCase
import io.konform.validation.ValidationErrors
import org.springframework.stereotype.Service

typealias checkWidgetCode = (String) -> Either<ValidationErrors, Pair<String, WidgetCode>>

@Service
class OrderApplication {

    @UseCase
    private fun checkWidgetCodeUseCase(widgetcode: String): Either<ValidationErrors, Pair<String, WidgetCode>> {

        // domain validates incoming
        val widgetCode = WidgetCode.of(widgetcode)

        return widgetCode.map { domainService(it) }
    }
    fun createCheckWidgetCodeUseCase(): checkWidgetCode {
        // TODO retrieve authorities from spring security context here ?
        return { code -> checkWidgetCodeUseCase(code) }
    }

}
