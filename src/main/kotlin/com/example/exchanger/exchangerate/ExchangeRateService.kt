package com.example.exchanger.exchangerate

import com.example.exchanger.exchangerate.infrastructure.nbp.NbpClient
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP

@Service
internal class ExchangeRateService(
    private val nbpClient: NbpClient,
    private val repository: ExchangeRateRepository
) : ExchangeRateInitializer {

    fun findExchangeRate(sourceCurrency: String): ExchangeRate? =
        repository.findBySourceCurrency(sourceCurrency)

    override fun updateExchangeRates() {
        nbpClient.fetchExchangeRate()
            ?.let { setOf(it, prepareReverseExchangeRate(it)) }
            ?.let { repository.saveAll(it) }
    }

    private fun prepareReverseExchangeRate(exchangeRate: ExchangeRate): ExchangeRate =
        ExchangeRate(
            source = exchangeRate.target,
            target = exchangeRate.source,
            emittedAt = exchangeRate.emittedAt,
            rate = reverseExchangeRate(exchangeRate.rate)
        )

    private fun reverseExchangeRate(rate: BigDecimal): BigDecimal =
        BigDecimal.ONE.divide(rate, SCALE, HALF_UP)

    companion object {
        const val SCALE: Int = 4
    }
}