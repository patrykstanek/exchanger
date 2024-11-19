package com.example.exchanger.exchangerate.infrastructure.postgres

import com.example.exchanger.shared.Currency
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "exchange_rates")
data class ExchangeRateEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Enumerated(EnumType.STRING)
    val sourceCurrency: Currency,
    @Enumerated(EnumType.STRING)
    val targetCurrency: Currency,
    var emittedAt: LocalDate,
    @Column(precision = 10, scale = 4)
    var rate: BigDecimal
)