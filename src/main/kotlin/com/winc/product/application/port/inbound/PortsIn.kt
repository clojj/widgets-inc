package com.winc.product.application.port.inbound

import hexa.HEXA

@HEXA.PortInbound
typealias Transact<R> = suspend (suspend () -> R) -> R
