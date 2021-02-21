package com.winc.order.adapter.rest

import com.winc.order.domain.port.`in`.OrderApplication
import com.winc.order.infra.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.NestedServletException
import java.util.*

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
class OrderController(
    private val orderApplication: OrderApplication
) {

    @PostMapping("/orders", consumes = ["application/json"], produces = ["application/json"])
    suspend fun createOrder(@RequestBody newOrder: NewOrder): ResponseEntity<OrderReceipt> =
        orderApplication.createOrder(newOrder.toCreateOrderCommand())
            .fold({
                throw PayloadException(ErrorResponse("$it via global handler"))
            }) {
                ResponseEntity.ok(OrderReceipt(it.orderId))
            }

    @GetMapping("/widget") // TODO @PreAuthorize("admin")
    suspend fun checkWidgetCode(): ResponseEntity<String> {
        println("admin in thread ${Thread.currentThread().name}")

        // in different thread
        return withContext(Dispatchers.IO) {
            val result = async {
                val authorities = retrieveAuthorities()

                // TODO somehow pass authorities into application/domain
                val either = orderApplication.checkWidgetCode()("A999")

                return@async either.fold({ ResponseEntity.ok("errors: $it") }) { ResponseEntity.ok("authorities $authorities : $it") }
            }
            result.await()
        }
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
