package com.winc.product.adapter.`in`.rest

import arrow.core.Either
import arrow.core.Nel
import com.winc.product.application.service.CreateProductCommand
import com.winc.product.config.CreateProduct
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class ProductController(val createProduct: CreateProduct) {

    @PostMapping("/products", consumes = ["application/json"], produces = ["application/json"])
    suspend fun create(@RequestBody product: NewProduct): Either<Nel<String>, UUID> =
        createProduct.run {

            // TODO authorisation with DDD
            println(retrieveAuthorities())

            CreateProductCommand(product.code, product.name).exec()
        }
}

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

data class NewProduct(val code: String, val name: String)
