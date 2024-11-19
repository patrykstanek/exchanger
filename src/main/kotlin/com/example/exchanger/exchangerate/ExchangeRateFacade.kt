package com.example.exchanger.exchangerate

import com.example.exchanger.shared.Currency
import org.springframework.stereotype.Component

@Component
class ExchangeRateFacade(
    private val service: ExchangeRateService
) {
    fun findExchangeRate(sourceCurrency: Currency, targetCurrency: Currency): ExchangeRate =
        service.findExchangeRate(sourceCurrency, targetCurrency)

}