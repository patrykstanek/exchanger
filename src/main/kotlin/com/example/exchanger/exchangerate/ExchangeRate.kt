package com.example.exchanger.exchangerate

import com.example.exchanger.shared.Currency
import java.math.BigDecimal
import java.time.LocalDate

data class ExchangeRate(
    val source: Currency,
    val target: Currency,
    val emittedAt: LocalDate,
    val rate: BigDecimal,
)