package com.example.exchanger.account.infrastructure.postgres

import com.example.exchanger.balance.infrastructure.BalanceEntity
import jakarta.persistence.*

@Entity
@Table(name = "accounts")
class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val surname: String,
    @OneToMany(mappedBy = "account")
    val balances: List<BalanceEntity>? = null
)