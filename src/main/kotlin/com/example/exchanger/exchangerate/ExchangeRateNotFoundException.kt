package com.example.exchanger.exchangerate

import com.example.exchanger.shared.Currency

class ExchangeRateNotFoundException(sourceCurrency: Currency, targetCurrency: Currency) :
    RuntimeException("Exchange rate not found for $sourceCurrency to $targetCurrency")