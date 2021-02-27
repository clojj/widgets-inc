package com.winc.product.application.port.outbound

import arrow.core.Either
import arrow.core.Nel
import com.winc.product.domain.model.Product
import hexa.HEXA
import java.util.*

@HEXA.PortOutbound
typealias SaveProduct = suspend (product: Product) -> Either<Nel<String>, UUID>

@HEXA.PortOutbound
typealias UpdateProduct = suspend (product: Product) -> Either<Nel<String>, Product>
