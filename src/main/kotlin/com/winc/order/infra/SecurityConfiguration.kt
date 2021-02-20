package com.winc.order.infra

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

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
            .antMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/hello").hasAnyRole("ADMIN", "USER")
            .and()
            .csrf().disable()
    }
}
