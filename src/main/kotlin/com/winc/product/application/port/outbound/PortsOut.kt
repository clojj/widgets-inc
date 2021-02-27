package com.winc.product.application.port.outbound

import arrow.core.Either
import com.winc.product.domain.model.Error
import com.winc.product.domain.model.Product.NewProduct
import com.winc.product.domain.model.Product.ValidProduct
import hexa.HEXA

@HEXA.PortOutbound
typealias SaveProduct = suspend (product: NewProduct) -> Either<Error, ValidProduct>

@HEXA.PortOutbound
typealias UpdateProduct = suspend (product: ValidProduct) -> Either<Error, ValidProduct>
