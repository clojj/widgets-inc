package com.winc.product.domain.model

import arrow.core.Nel
import arrow.core.Validated
import arrow.core.nonEmptyList
import arrow.typeclasses.Semigroup
import com.capraro.kalidation.constraints.function.notBlank
import com.capraro.kalidation.dsl.constraints
import com.capraro.kalidation.dsl.property
import com.capraro.kalidation.dsl.validationSpec
import ddd.DDD

inline val String.value: String
    get() = this

// TODO as @Aggregate
@DDD.Entity
data class Product(val code: ProductCode, val name: String) {

    companion object {
        val spec = validationSpec {
            constraints<String> {
                property(String::value) {
                    notBlank("a product name must not be blank")
                }
            }
        }

        fun of(code: String, name: String): Validated<Nel<String>, Product> {
            val productCode: Validated<Nel<String>, ProductCode> = ProductCode.of(code)
            // TODO better kalidation alignment
            val validatedName: Validated<Nel<String>, String> = spec.validateType(name).mapLeft { Nel.fromListUnsafe(it.toList().map { "${it.fieldName} has issue: ${it.message}" }) }
            return Validated.mapN(Semigroup.nonEmptyList(), productCode, validatedName) { code_, name_ -> Product(code_, name_) }
        }
    }
}
