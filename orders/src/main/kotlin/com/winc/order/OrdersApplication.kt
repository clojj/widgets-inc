package com.winc.order

import com.winc.order.adapter.persistence.jpa.OrderEntity
import com.winc.order.adapter.persistence.jpa.OrderRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

interface Logging {

    fun Logger.debug(block: () -> String) {
        if (isDebugEnabled) {
            debug(block())
        }
    }

}

inline fun <reified T : Logging> T.logger(): Logger =
    LoggerFactory.getLogger(T::class.java.name)

@SpringBootApplication
class OrdersApplication : ApplicationListener<ApplicationReadyEvent>, Logging {

    private val log = logger()

    private fun doIt() {
        log.info("did it!")
    }

    override fun onApplicationEvent(p0: ApplicationReadyEvent) {
        log.debug { ("DEBUG ${doIt()}") }
    }

}

fun main(args: Array<String>) {
    runApplication<OrdersApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}

@Configuration
@EnableJpaRepositories(basePackages = ["com.winc.order.adapter.persistence.jpa"])
@EnableJpaAuditing
class JpaConfig

@RestController
class OrderController(val orderService: OrderService) : Logging {

    private val log = logger()

    @PostMapping("/orders", consumes = ["application/json"])
    fun create(@RequestBody order: OrderDTO): Boolean {

/*
        val codec: UuidCodec<String> = StringCodec()
        product.uuid = codec.encode(UuidCreator.getTimeOrdered())
*/
        val orderEntity = OrderEntity().apply {
            code = order.code
        }
        val savedOrder = orderService.save(orderEntity)
        log.info("order saved: $savedOrder")

        return true
    }
}

data class OrderDTO(val code: String)

@Service
@Transactional
class OrderService(val orderRepository: OrderRepository) {

    fun save(orderEntity: OrderEntity) =
        orderRepository.save(orderEntity)

}