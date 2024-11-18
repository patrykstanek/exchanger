package com.example.exchanger.account.infrastructure.http

import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal

internal data class AccountDto(

    val name: String,
    val surname: String,
    @DecimalMin(value = "0.00")
    val initialBalance: BigDecimal
)
