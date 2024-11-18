package com.example.exchanger.exchangerate

import org.springframework.stereotype.Component

@Component
internal class ExchangeRateFacade(
    private val service: ExchangeRateService
) {
    fun findExchangeRate(sourceCurrency: String): ExchangeRate? =
        service.findExchangeRate(sourceCurrency)

}