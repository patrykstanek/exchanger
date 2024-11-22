package com.example.exchanger.account.infrastructure.http

class InvalidAmountException : RuntimeException("Amount must be 0 or greater")