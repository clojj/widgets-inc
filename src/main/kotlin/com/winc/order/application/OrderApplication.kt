package com.winc.order.application

import arrow.core.Either
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.right
import com.winc.order.domain.model.Order
import com.winc.order.domain.model.value.WidgetCode
import com.winc.order.domain.service.domainService
import com.winc.order.infra.NewOrder
import com.winc.order.infra.OrderReceipt
import ddd.UseCase
import io.konform.validation.ValidationErrors
import org.springframework.stereotype.Service
import java.util.*

typealias checkWidgetCode = (String) -> Either<ValidationErrors, Pair<String, WidgetCode>>

@Service
class OrderApplication {

    @UseCase
    private fun checkWidgetCodeUseCase(widgetcode: String): Either<ValidationErrors, Pair<String, WidgetCode>> {

        // domain validates incoming
        val widgetCode = WidgetCode.of(widgetcode).toEither()

        return widgetCode.map { domainService(it) }
    }

    fun createCheckWidgetCodeUseCase(): checkWidgetCode {
        // TODO retrieve authorities from spring security context here ?
        return { code -> checkWidgetCodeUseCase(code) }
    }

    @UseCase
    // TODO general sealed hierarchy for error type (ValidationErrors is just one choice-type)

    // TODO onion violations:
    //  NewOrder -> NewOrderCommand
    //  move UUID to Order, return just UUID
    suspend fun createOrder(newOrder: NewOrder): Either<ValidationErrors, OrderReceipt> =
        either {
            val validateOrder = validateOrder(newOrder)()
            val order = processOrder(validateOrder)()
            order
        }

    suspend fun validateOrder(newOrder: NewOrder): Validated<ValidationErrors, Order> =
        // TODO Order.of with all validations
        WidgetCode.of(newOrder.code).map { Order(it, newOrder.amount) }


    fun processOrder(validateOrder: Order): Either<ValidationErrors, OrderReceipt> =
          OrderReceipt(UUID.randomUUID(), validateOrder.code.code, validateOrder.amount).right()

}
