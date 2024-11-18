package com.example.exchanger.exchangerate

interface ExchangeRateRepository {
    fun saveAll(exchangeRates: Set<ExchangeRate>)
    fun findBySourceCurrency(sourceCurrency: String): ExchangeRate?
}