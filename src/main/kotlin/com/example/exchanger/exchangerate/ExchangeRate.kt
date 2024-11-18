package com.example.exchanger.exchangerate

import java.math.BigDecimal
import java.time.LocalDate

data class ExchangeRate(
    val source: String,
    val target: String,
    val emittedAt: LocalDate,
    val rate: BigDecimal,
)