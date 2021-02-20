package com.winc.order.domain.model

import arrow.core.Nel
import arrow.core.Validated
import arrow.core.extensions.nonemptylist.semigroup.semigroup
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

        fun of(code: String, amount: Int): Validated<Nel<String>, Order> {
            val widgetCode: Validated<Nel<String>, WidgetCode> = WidgetCode.of(code)
            // TODO better kalidation alignment
            val validatedAmount: Validated<Nel<String>, Int> = spec.validateType(amount).mapLeft { Nel.fromListUnsafe(it.toList().map { "${it.fieldName} has issue: ${it.message}" }) }
            return Validated.mapN(Nel.semigroup(), widgetCode, validatedAmount) { code, amount -> Order(code, amount) }
        }
    }
}
