package com.example.exchanger.balance.infrastructure

class BalancesNotFoundException(accountId: Long) : RuntimeException("No balance found with id = $accountId")