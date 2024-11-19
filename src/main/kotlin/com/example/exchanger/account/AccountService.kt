package com.example.exchanger.account

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
class AccountService(
    private val repository: AccountRepository,
    private val balanceRepository: BalancePostgresRepository,
    private val exchangeRateFacade: ExchangeRateFacade
) {
    @Transactional
    fun createAccount(account: Account): AccountId {
        return repository.save(account)
            .also { balanceRepository.saveAll(buildAccountBalances(account, it)) }
            .let { AccountId(it.id!!) }
    }

    @Transactional
    fun exchange(accountId: Long, exchangeDetails: ExchangeDetails) {
        val accountBalances =
            balanceRepository.findByAccountId(accountId).ifEmpty { throw BalancesNotFoundException(accountId) }
        validateAccountsCurrency(accountBalances, exchangeDetails)
        withdrawMoneyFromSource(accountBalances, exchangeDetails)

        val exchangeRate =
            exchangeRateFacade.findExchangeRate(exchangeDetails.sourceCurrency, exchangeDetails.targetCurrency)
        val exchangedAmount = Money(exchangeDetails.amountToExchange, exchangeDetails.sourceCurrency)
            .times(exchangeRate.rate)

        sendToTarget(accountBalances, exchangeDetails, exchangedAmount)

        balanceRepository.saveAll(accountBalances)
    }

    fun findById(accountId: Long): Account {
        return repository.findById(accountId).toDomain()
    }

    private fun sendToTarget(
        accountBalances: List<BalanceEntity>,
        exchangeDetails: ExchangeDetails,
        exchangedAmount: Money
    ) {
        val targetBalance = accountBalances.find { it.currency == exchangeDetails.targetCurrency }!!
        targetBalance.plus(exchangedAmount.amount)
    }

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

    private fun validateEnoughMoney(sourceBalance: BigDecimal, amountToWithdraw: BigDecimal) {
        if (sourceBalance < amountToWithdraw) throw RuntimeException("Not enough money")
    }

    private fun validateAccountsCurrency(accountBalances: List<BalanceEntity>, exchangeDetails: ExchangeDetails) {
        if (!(accountBalances.any { it.currency == exchangeDetails.sourceCurrency } && accountBalances.any { it.currency == exchangeDetails.targetCurrency })) {
            throw RuntimeException("Account can not handle exchange in these currencies")
        }
    }

    private fun buildAccountBalances(account: Account, accountEntity: AccountEntity) =
        setOf(
            buildAccountBalance(account.balances.first(), accountEntity),
            buildAccountBalance(Money(BigDecimal.ZERO, Currency.USD), accountEntity)
        )

    private fun buildAccountBalance(money: Money, accountEntity: AccountEntity) =
        BalanceEntity(
            currency = money.currency,
            balance = money.amount,
            account = accountEntity
        )

}

private fun AccountEntity.toDomain(): Account =
    Account(
        name = this.name,
        surname = this.surname,
        balances = this.balances
            ?.map { Money(it.balance, it.currency) }
            ?.toSet()
            ?: emptySet()
    )
