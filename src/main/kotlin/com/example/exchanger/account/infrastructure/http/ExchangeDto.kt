package com.example.exchanger.account.infrastructure.http

import com.example.exchanger.shared.Currency
import java.math.BigDecimal

class ExchangeDto(
    val amount: BigDecimal,
    val sourceCurrency: Currency,
    val targetCurrency: Currency
) {
    init {
        if (amount < BigDecimal.ZERO) {
            throw InvalidAmountException()
        }
    }
}