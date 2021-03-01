package com.winc.product.adapter.outbound.persistence.r2dbc

import com.winc.product.domain.model.Product.NewProduct
import com.winc.product.domain.model.Product.ValidProduct
import com.winc.product.domain.model.ProductCode
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("winc_product.product_entity")
data class ProductEntity(@Id var id: Long? = null, val code: String, val name: String)

fun NewProduct.toEntity() =
    ProductEntity(code = code.value, name = name)

fun ValidProduct.toEntity() =
    ProductEntity(id, code.value, name)

fun ProductEntity.toDomain(): ValidProduct =
    if (id == null)
        throw TODO("uuid is null")
    else
        ProductCode.of(code)
            .fold({
                throw TODO("invalid database code")
            }) {
                ValidProduct(id = id!!, code = it, name = name)
            }
