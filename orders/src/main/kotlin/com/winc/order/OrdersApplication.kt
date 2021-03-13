package com.winc.order

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableWebSecurity
@EnableJdbcRepositories(basePackages = ["com.winc.order.adapter.persistence.datajdbc"])
class OrdersApplication

fun main(args: Array<String>) {
    runApplication<OrdersApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
