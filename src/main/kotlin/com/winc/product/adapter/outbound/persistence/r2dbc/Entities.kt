package com.winc.product.adapter.outbound.persistence.r2dbc

import com.winc.product.domain.model.Product
import com.winc.product.domain.model.ProductCode
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table
data class ProductEntity(@Id var uuid: UUID? = null, val code: String, val name: String)

fun Product.toEntity() =
    ProductEntity(code = code.value, name = name)

fun ProductEntity.toDomain(): Product =
    ProductCode.of(code)
        .fold({
            throw TODO("invalid database code")
        }) {
            Product(uuid = uuid, code = it, name = name)
        }
