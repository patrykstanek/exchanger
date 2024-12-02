package com.example.exchanger.account

import com.example.exchanger.account.infrastructure.http.AccountBalancesDto
import com.example.exchanger.account.infrastructure.postgres.AccountEntity
import com.example.exchanger.balance.infrastructure.BalanceEntity
import com.example.exchanger.balance.infrastructure.BalancePostgresRepository
import com.example.exchanger.balance.infrastructure.BalancesNotFoundException
import com.example.exchanger.exchangerate.ExchangeRateFacade
import com.example.exchanger.shared.AccountId
import com.example.exchanger.shared.Currency
import com.example.exchanger.shared.Money
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
internal class AccountService(
    private val repository: AccountRepository,
    private val balanceRepository: BalancePostgresRepository,
    private val exchangeRateFacade: ExchangeRateFacade
) {

    @Transactional
    fun createAccount(account: Account): AccountId {
        val accountEntity = repository.save(account.toEntity())
        return buildAccountBalances(account, accountEntity)
            .let { balanceRepository.saveAll(it) }
            .let { AccountId(accountEntity.id!!) }
    }

    @Transactional
    fun exchange(accountId: Long, exchangeDetails: ExchangeDetails): AccountBalancesDto {
        val accountBalances =
            balanceRepository.findByAccountId(accountId)
                .ifEmpty { throw BalancesNotFoundException(accountId) }
                .also { validateAccountsCurrency(it, exchangeDetails) }
                .also { withdrawMoneyFromSource(it, exchangeDetails) }

        val exchangedAmount = calculateAmountInTargetCurrency(exchangeDetails)
        sendToTarget(accountBalances, exchangeDetails, exchangedAmount)

        return balanceRepository.saveAll(accountBalances).toSet()
            .let { buildAccountBalances(AccountId(accountId), it) }
    }

    fun findById(accountId: Long): Account =
        repository.findById(accountId).toDomain()

    private fun withdrawMoneyFromSource(
        accountBalances: List<BalanceEntity>,
        exchangeDetails: ExchangeDetails
    ) {
        accountBalances.filter { it.currency == exchangeDetails.sourceCurrency }
            .onEach {
                validateEnoughMoney(it.balance, exchangeDetails.amountToExchange)
                it.minus(exchangeDetails.amountToExchange)
            }
    }

    private fun calculateAmountInTargetCurrency(exchangeDetails: ExchangeDetails): Money {
        val exchangeRate =
            exchangeRateFacade.findExchangeRate(exchangeDetails.sourceCurrency, exchangeDetails.targetCurrency)
        return Money(exchangeDetails.amountToExchange, exchangeDetails.sourceCurrency)
            .times(exchangeRate.rate)
    }

    private fun sendToTarget(
        accountBalances: List<BalanceEntity>,
        exchangeDetails: ExchangeDetails,
        exchangedAmount: Money
    ) {
        val targetBalance = accountBalances.find { it.currency == exchangeDetails.targetCurrency }!!
        targetBalance.plus(exchangedAmount.amount)
    }

    private fun validateEnoughMoney(sourceBalance: BigDecimal, amountToWithdraw: BigDecimal) {
        if (sourceBalance < amountToWithdraw) throw InsufficientFundsException("Insufficient funds: Requested $amountToWithdraw but only $sourceBalance available.")
    }

    private fun validateAccountsCurrency(accountBalances: List<BalanceEntity>, exchangeDetails: ExchangeDetails) {
        if (!(accountBalances.any { it.currency == exchangeDetails.sourceCurrency } && accountBalances.any { it.currency == exchangeDetails.targetCurrency })) {
            throw RuntimeException("Account can not handle exchange in these currencies")
        }
    }

    private fun buildAccountBalances(account: Account, accountEntity: AccountEntity): List<BalanceEntity> =
        listOf(
            buildAccountBalance(account.balances.first(), accountEntity),
            buildAccountBalance(Money(BigDecimal.ZERO, Currency.USD), accountEntity)
        )

    private fun buildAccountBalance(money: Money, accountEntity: AccountEntity) =
        BalanceEntity(
            currency = money.currency,
            balance = money.amount,
            account = accountEntity
        )

    fun buildAccountBalances(accountId: AccountId, balanceEntities: Set<BalanceEntity>) =
        AccountBalancesDto(
            accountId = accountId,
            balances = balanceEntities.map {
                Money(it.balance, it.currency)
            }.toSet()
        )
}

private fun AccountEntity.toDomain(): Account =
    Account(
        name = this.name,
        surname = this.surname,
        balances = this.balances
            ?.map { Money(it.balance, it.currency) }
            ?.toSet()
            ?: setOf()
    )

private fun Account.toEntity(): AccountEntity =
    AccountEntity(
        name = this.name,
        surname = this.surname
    )
