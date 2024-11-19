package com.example.exchanger.account.infrastructure.postgres

class AccountNotFoundException(accountId: Long) : RuntimeException("Account with id = $accountId not found")