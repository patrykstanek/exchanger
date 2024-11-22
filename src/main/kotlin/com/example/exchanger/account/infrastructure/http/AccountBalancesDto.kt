package com.example.exchanger.account.infrastructure.http

import com.example.exchanger.shared.AccountId
import com.example.exchanger.shared.Money

data class AccountBalancesDto(
    val accountId: AccountId,
    val balances: Set<Money>
)
