package com.example.exchanger.exchangerate

import com.example.exchanger.shared.logger
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!test")
@Component
internal class InitialExchangeRateInitializer(
    private val service: ExchangeRateInitializer
) {

    @PostConstruct
    fun initializeInitialExchangeRates() {
        service.updateExchangeRates()
        log.info("Initial exchange rates updated.")
    }

    companion object {
        private val log by logger()
    }
}
