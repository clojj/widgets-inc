package com.winc.order.infra

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

const val springJpaEntities = "com.winc.order.adapter.persistence.spring_jpa"
@SpringBootApplication
@EntityScan(springJpaEntities)
class WidgetIncApplication

fun main(args: Array<String>) {
    runApplication<WidgetIncApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
