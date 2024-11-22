package com.example.exchanger.account.infrastructure.http

import java.math.BigDecimal

data class AccountDto(
    val name: String,
    val surname: String,
    val initialBalance: BigDecimal
) {
    init {
        if (initialBalance < BigDecimal.ZERO) {
            throw InvalidAmountException()
        }
        if (name.isEmpty()) {
            throw FieldValidationException("name must not be empty")
        }
        if (surname.isEmpty()) {
            throw FieldValidationException("surname must not be empty")
        }
    }
}
