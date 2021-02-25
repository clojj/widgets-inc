package com.winc.product.application.port.inbound

typealias Transact<R> = suspend (suspend () -> R) -> R
