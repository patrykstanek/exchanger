package com.example.exchanger.account.infrastructure.http

import com.example.exchanger.account.Account
import com.example.exchanger.account.AccountFacade
import com.example.exchanger.account.ExchangeDetails
import com.example.exchanger.shared.AccountId
import com.example.exchanger.shared.Currency
import com.example.exchanger.shared.Money
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/accounts")
internal class AccountEndpoint(
    private val accountFacade: AccountFacade
) {

    @PostMapping
    fun createAccount(@RequestBody accountDto: AccountDto): ResponseEntity<AccountId> {
        return accountFacade.createAccount(accountDto.toDomain())
            .let { ResponseEntity.ok(it) }
    }

    @PatchMapping("/{accountId}/exchange")
    fun exchange(@PathVariable accountId: Long, @RequestBody exchangeDto: ExchangeDto): ResponseEntity<Any> {
        return accountFacade.exchange(accountId, exchangeDto.toDomain())
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{accountId}")
    fun getAccountInfo(@PathVariable accountId: Long): ResponseEntity<AccountDetailsDto> {
        return accountFacade.findById(accountId)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }

    companion object {
        val DEFAULT_CURRENCY = Currency.PLN
    }

    private fun AccountDto.toDomain(): Account =
        Account(
            name = this.name,
            surname = this.surname,
            balances = setOf(Money(this.initialBalance, DEFAULT_CURRENCY))
        )

    private fun Account.toDto(): AccountDetailsDto =
        AccountDetailsDto(
            name = this.name,
            surname = this.surname,
            balances = this.balances
        )

    private fun ExchangeDto.toDomain(): ExchangeDetails =
        ExchangeDetails(
            amountToExchange = this.amount,
            sourceCurrency = sourceCurrency,
            targetCurrency = targetCurrency
        )
}
