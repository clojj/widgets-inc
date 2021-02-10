package com.winc.order.domain.model

import arrow.core.Validated
import arrow.core.list
import arrow.typeclasses.Semigroup
import com.capraro.kalidation.constraints.function.range
import com.capraro.kalidation.dsl.constraints
import com.capraro.kalidation.dsl.property
import com.capraro.kalidation.dsl.validationSpec
import com.winc.order.domain.model.value.WidgetCode
import ddd.DDD

inline val Int.value: Int
    get() = this

// @Aggregate ?
@DDD.Entity
data class Order(val code: WidgetCode, val amount: Int) {

    companion object {
        val spec = validationSpec {
            constraints<Int> {
                property(Int::value) {
                    range(5, 8)
                }
            }
        }

        fun of(code: String, amount: Int): Validated<List<String>, Order> {
            val widgetCode: Validated<List<String>, WidgetCode> = WidgetCode.of(code)
            // TODO better kalidation alignment
            val validatedAmount: Validated<List<String>, Int> = spec.validateType(amount).mapLeft { it.toList().map { "${it.fieldName} has issue: ${it.message}" } }
            return Validated.mapN(Semigroup.list(), widgetCode, validatedAmount) { code, amount -> Order(code, amount) }
        }
    }
}
