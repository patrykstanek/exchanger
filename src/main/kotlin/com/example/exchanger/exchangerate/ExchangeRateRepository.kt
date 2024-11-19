package com.example.exchanger.exchangerate

import com.example.exchanger.shared.Currency

interface ExchangeRateRepository {
    fun saveAll(exchangeRates: Set<ExchangeRate>)
    fun findBySourceCurrencyAndTargetCurrency(sourceCurrency: Currency, targetCurrency: Currency): ExchangeRate?
}