package com.example.exchanger.exchangerate.infrastructure.postgres

import com.example.exchanger.shared.Currency
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface ExchangeRatePostgresRepository : CrudRepository<ExchangeRateEntity, Long> {

    fun findBySourceCurrencyAndTargetCurrency(sourceCurrency: Currency, targetCurrency: Currency): ExchangeRateEntity?

}
