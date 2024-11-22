package com.example.exchanger.infrastructure

import com.example.exchanger.account.InsufficientFundsException
import com.example.exchanger.account.infrastructure.http.InvalidAmountException
import com.example.exchanger.account.infrastructure.postgres.AccountNotFoundException
import com.example.exchanger.balance.infrastructure.BalancesNotFoundException
import com.example.exchanger.exchangerate.ExchangeRateNotFoundException
import com.example.exchanger.exchangerate.infrastructure.nbp.NbpClientException
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.reactive.function.client.WebClientResponseException

@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice
class AppExceptionHandler {

    @ExceptionHandler(
        AccountNotFoundException::class,
        BalancesNotFoundException::class,
        ExchangeRateNotFoundException::class
    )
    fun handleNotFoundException(ex: Exception): ResponseEntity<Any?> {
        val errorHolder: ErrorHolder = ErrorHolder.builder()
            .status(HttpStatus.NOT_FOUND)
            .message(ex.message)
            .build()
        return buildResponseEntity(errorHolder)
    }

    @ExceptionHandler(InsufficientFundsException::class, InvalidAmountException::class)
    fun handleInsufficientFundsException(ex: InsufficientFundsException): ResponseEntity<Any?> {
        val errorHolder: ErrorHolder = ErrorHolder.builder()
            .status(HttpStatus.BAD_REQUEST)
            .message(ex.message)
            .build()
        return buildResponseEntity(errorHolder)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<Any?> {
        val errorHolder: ErrorHolder = ErrorHolder.builder()
            .status(HttpStatus.BAD_REQUEST)
            .message(
                String.format(
                    "The parameter '%s' of value '%s' could not be converted to type '%s'",
                    ex.name,
                    ex.value,
                    ex.requiredType
                )
            )
            .debugMessage(ex.message)
            .build()
        return buildResponseEntity(errorHolder)
    }

    @ExceptionHandler(NbpClientException::class)
    fun handleNbpClientException(ex: NbpClientException): ResponseEntity<Any?> {
        val statusCode = ex.getResponseStatusCode()
        val status = if (statusCode == null) HttpStatus.INTERNAL_SERVER_ERROR else HttpStatus.valueOf(statusCode)
        val errorHolder: ErrorHolder = ErrorHolder.builder()
            .status(status)
            .message("Dependency service unavailable error")
            .debugMessage(ex.message)
            .build()
        return buildResponseEntity(errorHolder)
    }

    private fun buildResponseEntity(errorHolder: ErrorHolder): ResponseEntity<Any?> {
        return ResponseEntity(errorHolder, errorHolder.status!!)
    }

    private fun Throwable.getResponseStatusCode(): Int? =
        when (this) {
            is WebClientResponseException -> this.statusCode.value()
            else -> null
        }
}
