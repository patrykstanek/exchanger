package com.example.exchanger.account

import com.example.exchanger.shared.Currency
import java.math.BigDecimal

data class ExchangeDetails(
    val amountToExchange: BigDecimal,
    val sourceCurrency: Currency,
    val targetCurrency: Currency
)