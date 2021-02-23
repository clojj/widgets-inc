package com.winc.product.adapter.out.persistence.r2dbc

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table
data class ProductEntity(@Id var uuid: UUID? = null, val code: String, val name: String)
