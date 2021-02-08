package com.winc.order.application

import arrow.core.*
import arrow.core.computations.either
import arrow.typeclasses.Semigroup
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
    private fun checkWidgetCodeUseCase(widgetcode: String): Either<List<String>, Pair<String, WidgetCode>> {

        // domain validates incoming
        val widgetCode = WidgetCode.of(widgetcode).toEither()

        return widgetCode.map { domainService(it) }
    }

    fun createCheckWidgetCodeUseCase(): (String) -> Either<List<String>, Pair<String, WidgetCode>> {
        // TODO retrieve authorities from spring security context here ?
        return { code -> checkWidgetCodeUseCase(code) }
    }

    @UseCase
    // TODO general sealed hierarchy for error type (ValidationErrors is just one choice-type)

    // TODO onion violations:
    //  NewOrder -> NewOrderCommand
    //  move UUID to Order, return just UUID
    suspend fun createOrder(newOrder: NewOrder): Either<List<String>, OrderReceipt> =
        either {
            val validatedOrder = validateOrder(newOrder)()
            val processedOrder = processOrder(validatedOrder)()
            processedOrder
        }

    suspend fun validateOrder(newOrder: NewOrder): Validated<List<String>, Order> {
        // TODO Order.of with all validations
//        Order.of(newOrder.code, newOrder.amount)

        val widgetCode: Validated<List<String>, WidgetCode> = WidgetCode.of(newOrder.code)
        val validatedAmount = if (newOrder.amount > 100)
            "amount!".nel().invalid()
        else
            newOrder.amount.valid()
        return Validated.mapN(Semigroup.list(), widgetCode, validatedAmount) { code, amount -> Order(code, amount) }
    }


    fun processOrder(validatedOrder: Order): Either<List<String>, OrderReceipt> =
        OrderReceipt(UUID.randomUUID(), validatedOrder.code.code, validatedOrder.amount).right()


}
