package com.winc.infra

import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.reactive.TransactionalOperator

@SpringBootApplication
@EnableWebSecurity
@ComponentScan(basePackages = ["com.winc.product"])
@EnableR2dbcRepositories(basePackages = ["com.winc.*.adapter.*.persistence.r2dbc"])
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

@Configuration
class Configurations(val connectionFactory: ConnectionFactory) {
    @Bean
    fun txWriteOperator(): TransactionalOperator = TransactionalOperator.create(R2dbcTransactionManager(connectionFactory), transactionDefinition)
}

