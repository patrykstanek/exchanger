package com.example.exchanger.account.infrastructure.http

import com.example.exchanger.shared.Money

data class AccountDetailsDto(
    private val name: String,
    private val surname: String,
    private val balances: Set<Money>
)
