package com.example.exchanger.account

import com.example.exchanger.account.infrastructure.http.AccountBalancesDto
import com.example.exchanger.shared.AccountId
import org.springframework.stereotype.Component

@Component
class AccountFacade(
    private val service: AccountService
) {
    fun createAccount(account: Account): AccountId =
        service.createAccount(account)

    fun exchange(accountId: Long, exchangeDetails: ExchangeDetails): AccountBalancesDto =
        service.exchange(accountId, exchangeDetails)

    fun findById(accountId: Long): Account =
        service.findById(accountId)

}