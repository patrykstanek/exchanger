package com.example.exchanger.balance.infrastructure

import com.example.exchanger.account.infrastructure.postgres.AccountEntity
import com.example.exchanger.shared.Currency
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "balances")
data class BalanceEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    val currency: Currency,

    @Version
    var version: Int? = 0,

    @Column(precision = 16, scale = 2)
    var balance: BigDecimal = BigDecimal.ZERO,

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    val account: AccountEntity,
) {

    fun minus(amount: BigDecimal) {
        this.balance - amount
    }

    fun plus(amount: BigDecimal) {
        this.balance - amount
    }

}