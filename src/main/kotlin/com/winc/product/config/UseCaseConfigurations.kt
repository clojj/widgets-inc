package com.winc.product.config

import arrow.core.Either
import com.winc.product.adapter.outbound.persistence.datajdbc.ProductRepository
import com.winc.product.adapter.outbound.persistence.datajdbc.saveProductAdapter
import com.winc.product.adapter.outbound.persistence.datajdbc.transactionAdapter
import com.winc.product.application.port.inbound.Transact
import com.winc.product.application.port.outbound.SaveProduct
import com.winc.product.application.service.CreateProductUseCase
import com.winc.product.domain.model.Error
import com.winc.product.domain.model.Product.ValidProduct
import ddd.DDD
import hexa.HEXA
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager

@DDD.UseCase
@HEXA.Config
@Component
class CreateProduct(productRepository: ProductRepository, txManager: PlatformTransactionManager) : CreateProductUseCase {
    override val transact: Transact<Either<Error, ValidProduct>> = transactionAdapter(txManager)
    override val saveProduct: SaveProduct = saveProductAdapter(productRepository)
}
