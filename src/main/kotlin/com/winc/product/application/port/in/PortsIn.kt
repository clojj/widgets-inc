package com.winc.product.application.port.`in`

typealias Transact<R> = suspend (suspend () -> R) -> R
