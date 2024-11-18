package com.example.exchanger.exchangerate.infrastructure.nbp

import com.example.exchanger.exchangerate.ExchangeRate
import com.example.exchanger.shared.logger
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate

@Component
class NbpClient(
    private val nbpWebclient: WebClient
) {

    fun fetchExchangeRate(): ExchangeRate? =
        nbpWebclient.get()
            .uri { createUri(it) }
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(ExchangeRateDto::class.java)
            .onErrorMap(this::handleError)
            .map { it.toDomain() }
            .block()

    private fun createUri(uriBuilder: UriBuilder): URI =
        uriBuilder.path(URI)
            .queryParam("format", JSON_FORMAT)
            .build()

    private fun handleError(exception: Throwable): Throwable {
        log.error("NbpClientException error during request: {} ", exception.localizedMessage, exception)
        return NbpClientException(exception)
    }

    private fun ExchangeRateDto.toDomain(): ExchangeRate =
        ExchangeRate(
            source = this.code,
            target = TARGET_CURRENCY,
            emittedAt = this.rates[0].effectiveDate,
            rate = this.rates[0].mid
        )

    companion object {
        const val URI: String = "/api/exchangerates/rates/a/usd"
        const val JSON_FORMAT: String = "json"
        const val TARGET_CURRENCY: String = "PLN"
        private val log by logger()
    }
}

data class RateDto(
    val effectiveDate: LocalDate,
    val mid: BigDecimal
)

data class ExchangeRateDto(
    val code: String,
    val rates: List<RateDto>
)