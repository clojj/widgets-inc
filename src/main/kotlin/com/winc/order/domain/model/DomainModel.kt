package com.winc.order.domain.model

import arrow.core.*
import com.winc.order.domain.model.value.WidgetCode
import io.konform.validation.ValidationError
import io.konform.validation.ValidationErrors
import org.jmolecules.ddd.annotation.Entity

@Entity
data class Order(val code: WidgetCode, val amount: Int) {

    companion object {
        fun of(widgetCode: WidgetCode, amount: Int): Validated<List<String>, Order> {
            return if (amount == 0 || amount > 100) {
                "amount!".nel().invalid()
            }
            else Order(widgetCode, amount).valid()
        }
    }
}
