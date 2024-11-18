package com.example.exchanger.exchangerate.infrastructure.postgres

import com.example.exchanger.exchangerate.ExchangeRate
import com.example.exchanger.exchangerate.ExchangeRateRepository
import org.springframework.stereotype.Component

@Component
internal class ExchangeRateRepositoryAdapter(
    private val repository: ExchangeRatePostgresRepository
) : ExchangeRateRepository {

    override fun saveAll(exchangeRates: Set<ExchangeRate>) {
        exchangeRates.onEach { save(it) }
    }

    override fun findBySourceCurrency(sourceCurrency: String): ExchangeRate? =
        repository.findBySourceCurrency(sourceCurrency)
            ?.toDomain()

    private fun save(exchangeRate: ExchangeRate) {
        val entity = repository.findBySourceCurrency(exchangeRate.source)
        if (entity == null) {
            exchangeRate.toEntity()
                .let(repository::save)
        } else {
            entity.rate = exchangeRate.rate
            entity.emittedAt = exchangeRate.emittedAt
            repository.save(entity)
        }
    }
}

private fun ExchangeRateEntity.toDomain(): ExchangeRate =
    ExchangeRate(
        source = this.sourceCurrency,
        target = this.targetCurrency,
        emittedAt = this.emittedAt,
        rate = this.rate
    )

private fun ExchangeRate.toEntity(): ExchangeRateEntity =
    ExchangeRateEntity(
        sourceCurrency = this.source,
        targetCurrency = this.target,
        emittedAt = this.emittedAt,
        rate = this.rate
    )