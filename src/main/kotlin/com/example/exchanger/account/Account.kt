package com.example.exchanger.account

import com.example.exchanger.shared.Money

data class Account(
    val name: String,
    val surname: String,
    val balances: Set<Money>
)