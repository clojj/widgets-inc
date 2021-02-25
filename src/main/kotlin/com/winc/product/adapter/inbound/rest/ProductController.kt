package com.winc.product.adapter.inbound.rest

import com.winc.product.application.service.CreateProductCommand
import com.winc.product.config.CreateProduct
import com.winc.product.config.ErrorResponse
import com.winc.product.config.PayloadException
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class ProductController(val createProduct: CreateProduct) {

    @PostMapping("/products", consumes = ["application/json"], produces = ["application/json"])
    suspend fun create(@RequestBody productDTO: NewProductDTO): ResponseEntity<ProductCreatedDTO> =
        createProduct.run {

            // TODO authorisation with DDD
            println(retrieveAuthorities())

            CreateProductCommand(productDTO.code, productDTO.name)
                .execute()
                .fold({
                    throw PayloadException(ErrorResponse("$it via global handler"))
                }) {
                    ResponseEntity.ok(ProductCreatedDTO(it))
                }
        }
}

data class NewProductDTO(val code: String, val name: String)

data class ProductCreatedDTO(val orderId: UUID)

private suspend fun retrieveAuthorities(): List<String> {
    println("retrieve authorities in thread ${Thread.currentThread().name} with strategy ${SecurityContextHolder.getContextHolderStrategy().javaClass.simpleName}")

    val securityContext: SecurityContext? = SecurityContextHolder.getContext()
    securityContext?.run {
        authentication?.let {
            return it.authorities?.map { authority ->
                authority.authority
            }.orEmpty()
        }
    }
    return emptyList()
}
