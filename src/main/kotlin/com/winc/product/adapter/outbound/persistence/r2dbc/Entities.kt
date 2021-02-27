package com.winc.product.adapter.outbound.persistence.r2dbc

import com.winc.product.domain.model.Product.NewProduct
import com.winc.product.domain.model.Product.ValidProduct
import com.winc.product.domain.model.ProductCode
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table
data class ProductEntity(@Id var uuid: UUID? = null, val code: String, val name: String)

fun NewProduct.toEntity() =
    ProductEntity(code = code.value, name = name)

fun ValidProduct.toEntity() =
    ProductEntity(uuid, code.value, name)

fun ProductEntity.toDomain(): ValidProduct =
    if (uuid == null)
        throw TODO("uuid is null")
    else
        ProductCode.of(code)
            .fold({
                throw TODO("invalid database code")
            }) {
                ValidProduct(uuid = uuid!!, code = it, name = name)
            }
