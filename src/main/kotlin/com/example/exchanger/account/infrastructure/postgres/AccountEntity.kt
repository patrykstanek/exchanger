package com.example.exchanger.account.infrastructure.postgres

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "accounts")
class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val name: String,
    val surname: String,
    val balance: Long
)