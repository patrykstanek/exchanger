package com.example.exchanger.exchangerate.infrastructure.postgres

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "exchange_rates")
data class ExchangeRateEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(unique = true)
    val sourceCurrency: String,
    @Column(unique = true)
    val targetCurrency: String,
    var emittedAt: LocalDate,
    @Column(precision = 20, scale = 4)
    var rate: BigDecimal
)