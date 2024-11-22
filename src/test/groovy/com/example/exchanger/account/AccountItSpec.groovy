package com.example.exchanger.account

import com.example.exchanger.base.MvcIntegrationSpec
import com.example.exchanger.exchangerate.infrastructure.nbp.NbpResponse
import com.example.exchanger.shared.Currency
import org.springframework.http.HttpStatus
import spock.lang.Unroll

import java.math.RoundingMode

class AccountItSpec extends MvcIntegrationSpec {

    def setup() {
        cleanupBalanceRepository()
        cleanupAccountRepository()
        // insert exchange rates
        nbpStubbing.stubExchangeRateSucceededResponse(NbpResponse.exchangeRateResponse())
        exchangeRateInitializer.updateExchangeRates()
    }

    @Unroll
    def "should create account successfully for #name #surname with balance #balance"() {
        given: "A valid account DTO"
            def accountDto = accountDto(name, surname, balance)

        when: "The account creation endpoint is called"
            def responseBody = exchangerTestClient.createAccountExpectingSuccess(accountDto)

        then: "The response body contains a correct account id"
            responseBody.accountId == accountId

        where:
            name    | surname   | balance | accountId
            "John"  | "Doe"     | 1000.00 | 1
            "Jane"  | "Smith"   | 2000.00 | 2
            "Alice" | "Johnson" | 0.00    | 3
    }

    def "should exchange money successfully"() {
        given: "A valid exchange DTO"
            def amountToExchange = 500.00
            def exchangeDto = exchangeDto(amountToExchange)

        and: "exchanged amount"
            def exchangeRate = BigDecimal.ONE.divide(new BigDecimal(NbpResponse.EXCHANGE_RATE), 4, RoundingMode.HALF_UP)
            def exchangedAmount = amountToExchange.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP)

        and: "Create a new account"
            def name = "Jalina"
            def surname = "Balike"
            def initialBalance = 2000.00
            def accountDto = accountDto(name, surname, initialBalance)
            def accountId = exchangerTestClient.createAccountExpectingSuccess(accountDto).accountId

        when: "The exchange endpoint is called"
            def responseBody = exchangerTestClient.exchangeMoneyExpectingSuccess(accountId, exchangeDto)

        then: "The response body confirms the exchange"
            verifyAll(responseBody) {
                it.accountId == accountId
                it.balances.size() == 2
                it.balances.any {
                    it.currency == Currency.PLN
                    it.amount == initialBalance - amountToExchange
                }
                it.balances.any {
                    it.currency == Currency.USD
                    it.amount == exchangedAmount
                }
            }
    }

    def "should fetch account details successfully"() {
        given: "Create a new account"
            def name = "Jalina"
            def surname = "Balike"
            def initialBalance = 2000.00
            def accountDto = accountDto(name, surname, initialBalance)
            def accountId = exchangerTestClient.createAccountExpectingSuccess(accountDto).accountId

        when: "The get account details endpoint is called"
            def responseBody = exchangerTestClient.getAccountInfoExpectingSuccess(accountId)

        then: "The response contains account details"
            verifyAll(responseBody) {
                it.name == name
                it.surname == surname
                it.balances.any {
                    it.amount == initialBalance
                    it.currency == Currency.PLN
                }
                it.balances.any {
                    it.amount == 0
                    it.currency == Currency.USD
                }
            }
    }

    def "should handle account not found error"() {
        given: "A non-existent account ID"
            def accountId = 999L

        expect: "The get account details endpoint is called and 404 is returned"
            exchangerTestClient.getAccountInfoExpectingError(accountId, HttpStatus.NOT_FOUND)

    }

    def "should validate input for account creation"() {
        given: "An invalid account DTO"
            def accountDto = accountDto("", "Doe", -1000.00)

        expect: "The account creation endpoint is called and 400 is returned"
            exchangerTestClient.createAccountExpectingError(accountDto, HttpStatus.BAD_REQUEST)
    }

    def accountDto(name, surname, initialBalance) {
        return """
            {
                "name"          : "${name}",
                "surname"       : "${surname}",
                "initialBalance": ${initialBalance}
            }
            """
    }

    def exchangeDto(amountToExchange) {
        return """
            {
              "amount": ${amountToExchange},
              "sourceCurrency": "PLN",
              "targetCurrency": "USD"
            }
            """
    }
}

