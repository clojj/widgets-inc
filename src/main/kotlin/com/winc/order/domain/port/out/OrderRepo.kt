package com.winc.order.domain.port.out

import reactor.core.publisher.Mono

interface OrderRepo<T, ID> {
    fun <S : T> save(var1: S): Mono<S>
}
