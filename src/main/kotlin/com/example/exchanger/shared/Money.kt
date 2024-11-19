package com.example.exchanger.shared

import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP

class Money(amount: BigDecimal, val currency: Currency) : Comparable<Money> {

    val amount: BigDecimal = amount.setScale(PRECISION, HALF_UP)

    override fun compareTo(other: Money): Int {
        validateSameCurrency(other)
        return amount.compareTo(other.amount)
    }

    operator fun times(multiplier: BigDecimal): Money =
        Money(amount.multiply(multiplier), currency)

    operator fun plus(other: Money): Money {
        validateSameCurrency(other)
        return this + other.amount
    }

    operator fun plus(amount: BigDecimal): Money =
        Money(this.amount + amount, currency)

    operator fun minus(other: Money): Money {
        validateSameCurrency(other)
        return Money(amount - other.amount, currency)
    }

    private fun validateSameCurrency(other: Money) {
        if (currency != other.currency) {
            throw IllegalArgumentException("Unable to compare money instances with different currency ($currency, ${other.currency}")
        }
    }

    override fun toString(): String =
        "Money(currency=$currency, amount=$amount)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Money

        if (currency != other.currency) return false
        if (amount != other.amount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = currency.hashCode()
        result = 31 * result + amount.hashCode()
        return result
    }

    companion object {
        const val PRECISION: Int = 2
    }
}
