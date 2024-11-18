package com.example.exchanger.base

import com.example.exchanger.AppRunner
import com.example.exchanger.exchangerate.ExchangeRateInitializer
import com.example.exchanger.exchangerate.infrastructure.postgres.ExchangeRatePostgresRepository
import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ActiveProfiles("test")
@SpringBootTest(classes = [AppRunner], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = [WireMockInitializer.class, PostgreSQLTestContainer.class])
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class IntegrationSpec extends Specification {

    @LocalServerPort
    int port

    @Autowired
    WireMockServer wireMock

    @Value("\${wiremock.port}")
    Integer wireMockPort

    @Autowired
    ExchangeRateInitializer exchangeRateInitializer

    @Autowired
    ExchangeRatePostgresRepository exchangeRateRepository

    def setup() {
        wireMock.resetAll()
    }

    protected void cleanupExchangeRateRepository() {
        exchangeRateRepository.deleteAll()
    }
}