package com.example.exchanger.exchangerate.infrastructure.postgres

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface ExchangeRatePostgresRepository : CrudRepository<ExchangeRateEntity, String> {

    fun findBySourceCurrency(sourceCurrency: String): ExchangeRateEntity?
}
