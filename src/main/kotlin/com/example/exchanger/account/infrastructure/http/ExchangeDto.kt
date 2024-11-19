package com.example.exchanger.account.infrastructure.http

import com.example.exchanger.shared.Currency
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

internal data class ExchangeDto(
    @NotNull
    @DecimalMin(value = "0.00")
    val amount: BigDecimal,
    @NotBlank
    val sourceCurrency: Currency,
    @NotBlank
    val targetCurrency: Currency
)
