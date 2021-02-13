package com.winc.order.infra

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


const val springJpaEntities = "com.winc.order.adapter.persistence.spring_jpa"

@SpringBootApplication
@EnableWebSecurity
@EntityScan(springJpaEntities)
@ComponentScan(basePackages = ["com.winc.order"])
class WidgetIncApplication

fun main(args: Array<String>) {
    runApplication<WidgetIncApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}

@Configuration
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        SecurityContextHolder.setStrategyName(MODE_INHERITABLETHREADLOCAL)

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
            .antMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/hello").hasAnyRole("ADMIN", "USER")
            .and()
            .csrf().disable()
    }
}
