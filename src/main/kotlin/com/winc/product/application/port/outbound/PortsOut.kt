package com.winc.product.application.port.outbound

import arrow.core.Either
import arrow.core.Nel
import com.winc.product.domain.model.Product
import java.util.*

typealias SaveProduct = suspend (product: Product) -> Either<Nel<String>, UUID>
