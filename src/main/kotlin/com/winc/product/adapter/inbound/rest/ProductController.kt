package com.winc.product.adapter.inbound.rest

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.winc.product.application.service.CreateProductCommand
import com.winc.product.config.CreateProduct
import com.winc.product.domain.model.Error
import hexa.HEXA
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@HEXA.AdapterInbound
@RestController
class ProductController(val createProduct: CreateProduct) {

    @PostMapping("/products", consumes = ["application/json"], produces = ["application/json"])
    suspend fun create(@RequestBody productDTO: NewProductDTO): Either<ResponseEntity<Error>, ResponseEntity<ProductDTO>> =
        createProduct.run {

            // TODO authorisation with DDD
            println(retrieveAuthorities())

            CreateProductCommand(productDTO.code, productDTO.name)
                .execute()
                .fold({
                    ResponseEntity.status(400).body(it).left()
                }) {
                    ResponseEntity.ok(ProductDTO(it.uuid, it.code.value, it.name)).right()
                }
        }
}

data class NewProductDTO(val code: String, val name: String)

data class ProductDTO(val orderId: UUID, val code: String, val name: String)

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
