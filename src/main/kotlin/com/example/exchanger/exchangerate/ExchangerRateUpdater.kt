package com.example.exchanger.exchangerate

import com.example.exchanger.shared.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ExchangerRateUpdater(
    private val exchangerRateInitializer: ExchangeRateInitializer
) {
    /**
     * According to https://nbp.pl/statystyka-i-sprawozdawczosc/kursy/informacja-o-terminach-publikacji-kursow-walut/
     * exchange rates are publish between 11:45 - 12:15 a.m. every workday
     */
    @Scheduled(cron = "\${exchange-rate.scheduler.timer-rate}", zone = "Europe/Warsaw")
    fun performTask() {
        log.info("Started update exchange rates task, executed at: ${LocalDateTime.now()}")
        exchangerRateInitializer.updateExchangeRates()
        log.info("Finished update exchange rates task")
    }

    companion object {
        private val log by logger()
    }
}