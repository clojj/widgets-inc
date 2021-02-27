package com.winc.product.config

import com.winc.product.domain.model.Error.InfraError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.util.NestedServletException
import com.winc.product.domain.model.Error as ProductError

data class PayloadException(val error: ProductError) : Throwable()

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(value = [Throwable::class])
    fun handleException(throwable: Throwable): ResponseEntity<ProductError> =
        when (throwable) {
            is NestedServletException ->
                when (throwable.cause) {
                    is PayloadException -> ResponseEntity.badRequest()
                        .body(((throwable.cause) as PayloadException).error)
                    else -> ResponseEntity.badRequest().body(InfraError(throwable.message ?: "UNKNOWN ERROR"))
                }
            else -> ResponseEntity.badRequest().body(InfraError(throwable.message ?: "UNKNOWN ERROR"))
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
