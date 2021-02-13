package com.winc.order.adapter.rest

import com.winc.order.domain.ports.incoming.OrderApplication
import com.winc.order.infra.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.NestedServletException

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
                ResponseEntity.ok(OrderReceipt(orderId))
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
