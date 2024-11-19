package com.example.exchanger.exchangerate

import com.example.exchanger.base.MvcIntegrationSpec
import com.example.exchanger.exchangerate.infrastructure.nbp.NbpClientException
import com.example.exchanger.exchangerate.infrastructure.nbp.NbpStubbingTestService
import org.springframework.beans.factory.annotation.Autowired

import java.math.RoundingMode
import java.time.LocalDate

import static com.example.exchanger.exchangerate.infrastructure.nbp.NbpResponse.*
import static com.example.exchanger.shared.Currency.PLN
import static com.example.exchanger.shared.Currency.USD
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

class ExchangeRateItSpec extends MvcIntegrationSpec {

    @Autowired
    NbpStubbingTestService nbpStubbing

    @Autowired
    ExchangeRateFacade exchangeRateFacade

    def setup() {
        cleanupExchangeRateRepository()
    }

    def "should save fetched exchange rate and then find them"() {
        given:
            def rate = new BigDecimal(EXCHANGE_RATE)
            nbpStubbing.stubExchangeRateSucceededResponse(exchangeRateResponse(rate.toString()))
        when:
            exchangeRateInitializer.updateExchangeRates()
        then:
            def savedExchangeRates = exchangeRateRepository.findAll().toList()
            nbpStubbing.getAllInteractions().size() == 1
            savedExchangeRates.size() == 2
            savedExchangeRates.any { it.sourceCurrency == USD && it.targetCurrency == PLN && it.rate == rate && it.emittedAt == LocalDate.parse(PUBLISH_DATE) }
            savedExchangeRates.any { it.sourceCurrency == PLN && it.targetCurrency == USD && it.rate == BigDecimal.ONE.divide(rate, 4, RoundingMode.HALF_UP) && it.emittedAt == LocalDate.parse(PUBLISH_DATE) }
        when: "should find usd/pln pair"
            def usdPlnPair = exchangeRateFacade.findExchangeRate(USD, PLN)
        then:
            usdPlnPair.source == USD
            usdPlnPair.target == PLN
            usdPlnPair.rate == rate
            usdPlnPair.emittedAt == LocalDate.parse(PUBLISH_DATE)
    }

    def "should not save anything if the NBP API response is empty"() {
        given: "an empty response from the NBP API"
            nbpStubbing.stubExchangeRateEmptyResponse()

        when: "the service updates exchange rates"
            exchangeRateInitializer.updateExchangeRates()

        then: "no exchange rates are saved"
            nbpStubbing.getAllInteractions().size() == 1
            exchangeRateRepository.findAll().isEmpty()
    }

    def "should not save anything if the NBP API returns invalid data"() {
        given: "an invalid response from the NBP API"
            def invalidResponse = '''{ "invalid_field": "unexpected_value" }'''
            nbpStubbing.stubExchangeRateSucceededResponse(invalidResponse)

        when: "the service attempts to update exchange rates"
            exchangeRateInitializer.updateExchangeRates()

        then: "NbpClientException was thrown"
            thrown(NbpClientException)
    }

    def "should handle error when the NBP API is down"() {
        given: "the NBP API returns an error response"
            nbpStubbing.stubExchangeRateErrorResponse(INTERNAL_SERVER_ERROR)

        when: "the service tries to update exchange rates"
            exchangeRateInitializer.updateExchangeRates()

        then: "NbpClientException was thrown"
            thrown(NbpClientException)
    }

    def "should not create duplicate exchange rates when update is called multiple times"() {
        given: "a first valid response from the NBP API"
            nbpStubbing.stubExchangeRateSucceededResponse()
            def previousRate = new BigDecimal(EXCHANGE_RATE)

        and: "a content of second response"
            def updateRate = new BigDecimal("4.1105")
            def updateDate = "2024-11-18"

        when: "the service updates exchange rates for first time"
            exchangeRateInitializer.updateExchangeRates()

        then: "saved exchange rates"
            def savedExchangeRates = exchangeRateRepository.findAll().toList()
            nbpStubbing.getAllInteractions().size() == 1
            savedExchangeRates.size() == 2
            savedExchangeRates.any { it.sourceCurrency == USD && it.targetCurrency == PLN && it.rate == previousRate && it.emittedAt == LocalDate.parse(PUBLISH_DATE) }
            savedExchangeRates.any { it.sourceCurrency == PLN && it.targetCurrency == USD && it.rate == BigDecimal.ONE.divide(previousRate, 4, RoundingMode.HALF_UP) && it.emittedAt == LocalDate.parse(PUBLISH_DATE) }

        when: "the service updates exchange rates for second time"
            nbpStubbing.stubExchangeRateSucceededResponse(exchangeRateResponse(updateRate.toString(), updateDate))
            exchangeRateInitializer.updateExchangeRates()

        then: "updated exchange rates"
            def updatedExchangeRates = exchangeRateRepository.findAll().toList()
            nbpStubbing.getAllInteractions().size() == 2
            updatedExchangeRates.size() == 2
            updatedExchangeRates.any { it.sourceCurrency == USD && it.targetCurrency == PLN && it.rate == updateRate && it.emittedAt == LocalDate.parse(updateDate) }
            updatedExchangeRates.any { it.sourceCurrency == PLN && it.targetCurrency == USD && it.rate == BigDecimal.ONE.divide(updateRate, 4, RoundingMode.HALF_UP) && it.emittedAt == LocalDate.parse(updateDate) }
    }
}
