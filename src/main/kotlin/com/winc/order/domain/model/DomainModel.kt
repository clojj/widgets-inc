package com.winc.order.domain.model

import arrow.core.*
import arrow.typeclasses.Semigroup
import com.capraro.kalidation.constraints.function.range
import com.capraro.kalidation.dsl.constraints
import com.capraro.kalidation.dsl.property
import com.capraro.kalidation.dsl.validationSpec
import com.capraro.kalidation.spec.ValidationResult
import com.winc.order.domain.model.value.WidgetCode
import org.jmolecules.ddd.annotation.Entity

inline val Int.value: Int
    get() = this

@Entity
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
            val validatedAmount: Validated<List<String>, Int> = spec.validateType(amount).mapLeft { it.toList().map { it.fieldName + it.message } }
            return Validated.mapN(Semigroup.list(), widgetCode, validatedAmount) { code, amount -> Order(code, amount) }
        }
    }
}
