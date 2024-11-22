package com.example.exchanger.base

import com.example.exchanger.AppRunner
import com.example.exchanger.account.infrastructure.postgres.AccountPostgresRepository
import com.example.exchanger.balance.infrastructure.BalancePostgresRepository
import com.example.exchanger.exchangerate.ExchangeRateInitializer
import com.example.exchanger.exchangerate.infrastructure.nbp.NbpStubbingTestService
import com.example.exchanger.exchangerate.infrastructure.postgres.ExchangeRatePostgresRepository
import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ActiveProfiles("test")
@SpringBootTest(classes = [AppRunner], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = [WireMockInitializer.class, PostgreSQLTestContainer.class])
abstract class IntegrationSpec extends Specification {

    @LocalServerPort
    int port

    @Autowired
    WireMockServer wireMock

    @Autowired
    ExchangeRateInitializer exchangeRateInitializer

    @Autowired
    NbpStubbingTestService nbpStubbing

    @Autowired
    ExchangeRatePostgresRepository exchangeRateRepository

    @Autowired
    AccountPostgresRepository accountRepository

    @Autowired
    BalancePostgresRepository balanceRepository

    def setup() {
        wireMock.resetAll()
    }

    protected void cleanupExchangeRateRepository() {
        exchangeRateRepository.deleteAll()
    }

    protected void cleanupAccountRepository() {
        accountRepository.deleteAll()
    }

    protected void cleanupBalanceRepository() {
        balanceRepository.deleteAll()
    }
}