package com.winc.order.adapter.persistence.jpa

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.persistence.*


@Repository
interface OrderRepository : CrudRepository<OrderEntity, Long>

@Entity(name = "order_entity")
@EntityListeners(AuditingEntityListener::class)
class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    lateinit var code: String

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.MIN
        private set

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.MIN
        private set

    override fun toString(): String {
        return "OrderEntity(id=$id, code='$code', createdAt=$createdAt, updatedAt=$updatedAt)"
    }

}