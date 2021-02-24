package com.winc.product.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.util.NestedServletException

val transactionDefinition: TransactionDefinition = object : TransactionDefinition {
    override fun getIsolationLevel(): Int {
        return TransactionDefinition.ISOLATION_READ_COMMITTED
    }
}

@Component
class Configurations(val connectionFactory: ConnectionFactory) {
    @Bean
    fun transactionalOperator() = TransactionalOperator.create(R2dbcTransactionManager(connectionFactory), transactionDefinition)
}

data class ErrorResponse(val error: String)

data class PayloadException(val errorResponse: ErrorResponse) : Throwable()

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(value = [Throwable::class])
    fun handleException(throwable: Throwable): ResponseEntity<ErrorResponse> =
        when (throwable) {
            is NestedServletException ->
                when (throwable.cause) {
                    is PayloadException -> ResponseEntity.badRequest()
                        .body(((throwable.cause) as PayloadException).errorResponse)
                    else -> ResponseEntity.badRequest().body(ErrorResponse(throwable.message ?: "unknown error"))
                }
            else -> ResponseEntity.badRequest().body(ErrorResponse(throwable.message ?: "unknown error"))
        }
}

@Configuration
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL)

        auth.inMemoryAuthentication()
            .withUser("user").password(encoder().encode("user")).roles("USER")
            .and()
            .withUser("admin").password(encoder().encode("admin")).roles("ADMIN", "USER")
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(http: HttpSecurity) {
        http.httpBasic()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/products").hasAnyRole("ADMIN", "USER")
            .and()
            .csrf().disable()
    }
}
