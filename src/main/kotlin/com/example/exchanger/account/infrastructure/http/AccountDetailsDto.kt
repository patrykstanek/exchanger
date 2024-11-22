package com.example.exchanger.account.infrastructure.http

import com.example.exchanger.shared.Money

data class AccountDetailsDto(
    val name: String,
    val surname: String,
    val balances: Set<Money>
)
