package com.winc.infra

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.transaction.TransactionDefinition

@SpringBootApplication
@EnableWebSecurity
@ComponentScan(basePackages = ["com.winc.product"])
@EnableJdbcRepositories(basePackages = ["com.winc.product.adapter.outbound.persistence"])
class WidgetIncApplication

fun main(args: Array<String>) {
    runApplication<WidgetIncApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}

// TODO simplify
val transactionDefinition: TransactionDefinition = object : TransactionDefinition {
    override fun getIsolationLevel(): Int {
        return TransactionDefinition.ISOLATION_READ_COMMITTED
    }
}

