package com.example.exchanger.account.infrastructure.http

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/accounts")
internal class AccountEndpoint {

    @PostMapping
    fun createAccount(@RequestBody accountDto: AccountDto) =
        null
}