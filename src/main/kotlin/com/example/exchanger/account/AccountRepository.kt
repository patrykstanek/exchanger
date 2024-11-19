package com.example.exchanger.account

import com.example.exchanger.account.infrastructure.postgres.AccountEntity

interface AccountRepository {
    fun save(account: Account): AccountEntity
    fun findById(id: Long): AccountEntity
}