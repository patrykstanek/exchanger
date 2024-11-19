package com.example.exchanger.account.infrastructure.http

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

internal data class AccountDto(
    @NotBlank
    val name: String,
    @NotBlank
    val surname: String,
    @NotNull
    @DecimalMin(value = "0.00")
    val initialBalance: BigDecimal
)
