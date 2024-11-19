package com.example.exchanger.account.infrastructure.postgres

import com.example.exchanger.account.Account
import com.example.exchanger.account.AccountRepository
import org.springframework.stereotype.Component

@Component
internal class AccountRepositoryAdapter(
    private val repository: AccountPostgresRepository
) : AccountRepository {

    override fun save(account: Account): AccountEntity {
        return repository.save(account.toEntity())
    }

    override fun findById(id: Long): AccountEntity {
        return repository.findById(id)
            .orElseThrow { throw AccountNotFoundException(id) }
    }
}

private fun Account.toEntity(): AccountEntity =
    AccountEntity(
        name = this.name,
        surname = this.surname
    )
