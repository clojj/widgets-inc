package com.winc.order.infra

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableWebSecurity
@ComponentScan(basePackages = ["com.winc.order"])
@EnableR2dbcRepositories(basePackages = ["com.winc.order.adapter.persistence.r2dbc"])
class WidgetIncApplication

fun main(args: Array<String>) {
    runApplication<WidgetIncApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}

