package com.winc.order.infra

import com.winc.order.application.OrderApplication
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.NestedServletException
import java.util.*


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

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(value = [Throwable::class])
    fun handleException(throwable: Throwable): ResponseEntity<ErrorResponse> =
        when (throwable) {
            is NestedServletException -> when (throwable.cause) {
                is PayloadException -> ResponseEntity.badRequest()
                    .body(((throwable.cause) as PayloadException).errorResponse)
                else -> ResponseEntity.badRequest().body(ErrorResponse(throwable.message ?: "unknown error"))
            }
            else -> ResponseEntity.badRequest().body(ErrorResponse(throwable.message ?: "unknown error"))
        }
}

@RestController
class OrderController(val orderApplication: OrderApplication) {

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello\n"
    }

    @PostMapping("/orders", consumes = ["application/json"], produces = ["application/json"])
    suspend fun createWidget(@RequestBody newOrder: NewOrder): ResponseEntity<OrderReceipt> =
        // edge IO
        orderApplication.createOrder(newOrder.toCommand()).fold({
            throw PayloadException(ErrorResponse("$it via global handler"))
        }) {
            with (it) {
                ResponseEntity.ok(OrderReceipt(orderId, code, amount))
            }
        }

    @GetMapping("/admin")
    suspend fun admin(): ResponseEntity<ResponseEntity<String>> {
        println("admin in thread ${Thread.currentThread().name}")

/* in same thread
        coroutineScope {
            async {
                retrieveAuthorities()
            }
        }
*/

        // in different thread
        val responseEntity = withContext(Dispatchers.IO) {
            val result = async {
                val authorities = retrieveAuthorities()

                // TODO somehow pass authorities into application/domain
                val either = orderApplication.createCheckWidgetCodeUseCase()("A999")

                return@async either.fold({ ResponseEntity.ok("errors: $it") }) { ResponseEntity.ok("authorities $authorities : $it") }
            }
            val response = result.await()
            return@withContext ResponseEntity.ok(response)
        }
        return responseEntity
    }

    private suspend fun retrieveAuthorities(): List<String> {
        println("retrieve authorities in thread ${Thread.currentThread().name} with strategy ${SecurityContextHolder.getContextHolderStrategy().javaClass.simpleName}")

        val securityContext: SecurityContext? = SecurityContextHolder.getContext()
        securityContext?.run {
            authentication?.let {
                println(it.authorities)
                return it.authorities?.map { authority ->
                    authority.authority
                }.orEmpty()
            }
        }
        return emptyList()
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
